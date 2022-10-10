package com.example.smartermovieapp.presentation.movies_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.data.local.model.Trailer
import com.example.smartermovieapp.data.mappers.TRAILER_ERROR_URL
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.TransparentStatusBarHandler
import com.example.smartermovieapp.presentation.home.*
import com.example.smartermovieapp.utils.hasInternet
import com.example.smartermovieapp.utils.observeOnce
import com.example.smartermovieapp.utils.showInternetError
import dagger.android.AndroidInjection
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

class MoviesDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var movieDetailsVM: MoviesDetailsViewModel

    @Inject
    lateinit var cinemaVM: CinemaVM

    private lateinit var favouriteView: ImageView
    private lateinit var loadingImage: ImageView
    private lateinit var internetErrorText: TextView

    companion object{
        const val KEY_MOVIE_ID = "key_movie_id"
        const val ERROR_LOADING_SIMILAR_MOVIES_DEFAULT = "Similar movies could not be loaded. Please try again later."
        const val ERROR_LOADING_MOVIE_DETAILS_DEFAULT = "Movie details could not be loaded. Please try again later."
        const val ERROR_LOADING_TRAILER_DEFAULT = "Trailer could not be loaded. Please try again later."
        const val SUBINFO_SEPARATOR = "|"
        const val CHECK_INTERNET_ERROR_SUBSTRING = "host"
        const val ERROR_SIMILAR_MOVIES_NOT_FOUND = "Could not find similar movies."
        const val ERROR_NO_TRAILER_FOUND = "No trailer found for this movie."
        const val ERROR_NO_CINEMA_RUNNING_MOVIE = "Movie is not available in any cinema."
        const val ERROR_NO_CINEMAS_DEFAULT = "Could not load cinemas, please try again later."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        TransparentStatusBarHandler.initTransparentStatusBar(window)
        setContentView(R.layout.activity_movie_details)

        val movieId = intent.extras?.getInt(KEY_MOVIE_ID) ?: -1
        movieDetailsVM.loadData(movieId)

        favouriteView = findViewById(R.id.btn_favorite)
        loadingImage = findViewById(R.id.iv_loading)
        internetErrorText = findViewById(R.id.txt_internet_error)

        setupMovieDetailsObserver()
        setupSimilarMoviesObserver()
        setupTrailerObserver()
        setupFavouriteObserver()

        setupPreviousListener()
        setupFavouriteListener(movieId)
        setupCheckAvailabilityListener(movieId)
    }

    private fun setupFavouriteObserver() =
        movieDetailsVM.isFavoriteLiveData.observe(this) {
            setupFavouriteIcon(it)
        }

    private fun setupFavouriteIcon(favouriteData: Boolean?) =
        favouriteData?.let { isFavourite ->
            if(isFavourite) {
                favouriteView.setImageResource(R.drawable.ic_favourite_checked)
            } else {
                favouriteView.setImageResource(R.drawable.ic_favourite_unchecked)
            }
        }

    private fun setupFavouriteListener(movieId: Int) =
        favouriteView.setOnClickListener {
            movieDetailsVM.toggleFavourite(movieId)
        }

    private fun setupPreviousListener() =
        findViewById<ImageButton>(R.id.btn_previous).setOnClickListener {
            this@MoviesDetailsActivity.finish()
        }

    private fun setupCheckAvailabilityListener(movieId: Int) =
        findViewById<Button>(R.id.btn_check_availability).setOnClickListener {
            cinemaVM.getCinemasRunningMovieLiveData(movieId).observeOnce(this) {
                when(it) {
                    is Resource.Success -> it.data?.let { cinemasRunningMovie -> checkAvailability(movieId, cinemasRunningMovie) }
                    is Resource.Error -> showError(it.message, ERROR_NO_CINEMAS_DEFAULT)
                    is Resource.Loading -> {}
                }
            }
        }

    private fun checkAvailability(movieId: Int, cinemasRunningMovie: List<Cinema>) {
        if(cinemasRunningMovie.isEmpty()) {
            showNoCinemasRunningMovieError()
            return
        }

        startMovieAvailabilityActivity(movieId)
    }

    private fun startMovieAvailabilityActivity(movieId: Int) {
        val movieAvailabilityIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra(HomeActivity.KEY_REDIRECT_TO_AVAILABILITY, true)
            putExtra(KEY_MOVIE_ID_AVAILABILITY, movieId)
        }
        startActivity(movieAvailabilityIntent)
    }

    private fun showNoCinemasRunningMovieError() =
        Toast.makeText(this, ERROR_NO_CINEMA_RUNNING_MOVIE, Toast.LENGTH_LONG).show()

    private fun setupMovieDetailsObserver() =
        movieDetailsVM.movieLiveData.observe(this) {
            when(it) {
                is Resource.Success -> setupMovieDetails(it.data)
                is Resource.Error -> showError(it.message, ERROR_LOADING_MOVIE_DETAILS_DEFAULT)
                is Resource.Loading -> {}
            }
        }

    private fun setupMovieDetails(movieData: Movie?) =
        movieData?.let { movie ->
            setupPosterImage(movie.imageUrl)
            setupRatingStars(movie.score)
            findViewById<TextView>(R.id.txt_movie_title).text = movie.title
            findViewById<TextView>(R.id.txt_story_line_text).text = movie.overview
            setupSubinfo(movie.language, movie.genres, movie.minutes)
        }

    private fun setupSubinfo(language: String, genres: List<String>, minutes: Int) {
        val subinfoTextView = findViewById<TextView>(R.id.txt_subinfo)

        val languageText = language.replaceFirstChar { it.uppercase() }
        val genresText = genres.joinToString(", ")
        val durationText = getMovieTimeFormatted(minutes)
        val subinfoText = "$languageText $SUBINFO_SEPARATOR $genresText $SUBINFO_SEPARATOR $durationText"
        subinfoTextView.text = subinfoText
    }

    private fun setupRatingStars(rating: Float) {
        val ratingSingleDigit = floatToSingleDigitRoundedDown(rating)

        val fullStarsCount = ratingSingleDigit.toInt()
        val remainingFloatingPoint = ratingSingleDigit - fullStarsCount
        val isLastStarAtLeastHalf = remainingFloatingPoint > 0.01f // If at least .1 at the end, we add half a star
        val isLastStarFull = remainingFloatingPoint > 0.51f // If at least .6 at the end, we add a full star

        val starViews = getStarViews()

        for(i in 0 until fullStarsCount) {
            starViews[i].setImageResource(R.drawable.ic_full_star)
        }

        if(isLastStarFull) {
            starViews[fullStarsCount].setImageResource(R.drawable.ic_full_star)
        } else if (isLastStarAtLeastHalf) {
            starViews[fullStarsCount].setImageResource(R.drawable.ic_half_star)
        }
    }

    private fun getStarViews() =
        listOf<ImageView>(
            findViewById(R.id.img_star_1),
            findViewById(R.id.img_star_2),
            findViewById(R.id.img_star_3),
            findViewById(R.id.img_star_4),
            findViewById(R.id.img_star_5)
        )

    private fun floatToSingleDigitRoundedDown(number: Float): Float {
        val decimalFormat = DecimalFormat("#.#")
        decimalFormat.roundingMode = RoundingMode.FLOOR // If 4.59 -> 4.5; if 4.61 -> 4.6
        return decimalFormat.format(number).toFloat()
    }

    private fun getMovieTimeFormatted(minutes: Int): String {
        val hours = minutes / 60
        val extraMinutes = minutes % 60
        return "${hours}h${extraMinutes}m"
    }

    private fun setupPosterImage(imageUrl: String) =
        Glide.with(this)
            .load(imageUrl)
            .into(findViewById(R.id.iv_poster_image))

    private fun setupTrailerObserver() =
        movieDetailsVM.trailerLiveData.observe(this) {
            when(it) {
                is Resource.Success -> setupWatchTrailerListener(it.data)
                is Resource.Error -> showError(it.message, ERROR_LOADING_TRAILER_DEFAULT)
                is Resource.Loading -> {}
            }
        }

    private fun setupWatchTrailerListener(trailerData: Trailer?) =
        trailerData?.let { trailer ->
            findViewById<Button>(R.id.btn_watch_trailer).setOnClickListener {
                gotoUrl(trailer.trailerUrl)
            }
        }

    private fun gotoUrl(trailerUrl: String) {
        if(trailerUrl.isEmpty()) return

        if(!hasInternet(this)) {
            showInternetError(this)
            return
        }

        if (trailerUrl == TRAILER_ERROR_URL) {
            showError(ERROR_NO_TRAILER_FOUND, ERROR_NO_TRAILER_FOUND)
            return
        }

        val uri = Uri.parse(trailerUrl)
        val urlIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(urlIntent)
    }

    private fun setupSimilarMoviesObserver() =
        movieDetailsVM.similarMoviesLiveData.observe(this) {
            when(it) {
                is Resource.Success -> {
                    setupSimilarMoviesRecycler(it.data)
                    loadingImage.isVisible = false
                }
                is Resource.Error -> {
                    internetErrorText.isVisible = true
                    loadingImage.isVisible = false
                    it.message?.let { errorMessage ->
                        if(!errorMessage.contains(CHECK_INTERNET_ERROR_SUBSTRING)) {
                            internetErrorText.text = ERROR_SIMILAR_MOVIES_NOT_FOUND
                        }
                    }
                }
                is Resource.Loading -> loadingImage.isVisible = true
            }
        }

    private fun setupSimilarMoviesRecycler(similarMovies: List<Movie>?) =
        findViewById<RecyclerView>(R.id.rv_similar_movies).run {
            adapter = SimilarMoviesAdapter(
                similarMovies,
                this@MoviesDetailsActivity,
            )
            layoutManager = LinearLayoutManager(
                this@MoviesDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }

    private fun showError(message: String?, fallbackError: String) =
        Toast.makeText(this, message ?: fallbackError, Toast.LENGTH_LONG).show()

    fun startMovieDetailsScreenOfMovie(movieId: Int) {
        val intent = Intent(this, MoviesDetailsActivity::class.java)
        intent.putExtra(MoviesDetailsActivity.KEY_MOVIE_ID, movieId)
        startActivity(intent)
    }

}