package com.example.smartermovieapp.presentation.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Category
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.data.mappers.TRAILER_ERROR_URL
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.movies_details.MoviesDetailsActivity
import com.example.smartermovieapp.utils.hasInternet
import com.example.smartermovieapp.utils.showInternetError
import dagger.android.support.AndroidSupportInjection
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

class FragmentHomeScreen : Fragment() {

    @Inject
    lateinit var homeVM: HomeVM

    private lateinit var trailerRecyclerView: RecyclerView
    private lateinit var topRatedRecyclerView: RecyclerView
    private lateinit var upcomingRecyclerView: RecyclerView
    private lateinit var popularRecyclerView: RecyclerView

    private lateinit var loadingFrameLayout: FrameLayout
    private val loadingCategories: MutableSet<Category> = mutableSetOf()

    private lateinit var searchView: SearchView

    companion object {
        const val ERROR_LOADING_MOVIE_DATA_DEFAULT = "Could not load movie data. Please try again later."
        const val ERROR_LOADING_TRAILER_DEFAULT = "Could not load trailer. Please try again later."
        const val ERROR_NO_TRAILER_FOUND = "No trailer found for this movie."
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingFrameLayout = view.findViewById(R.id.overlay_loading)
        searchView = view.findViewById(R.id.sv_search_movie)

        trailerRecyclerView = view.findViewById(R.id.rv_trailers)
        trailerRecyclerView.setHasFixedSize(true)
        trailerRecyclerView.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        topRatedRecyclerView = view.findViewById(R.id.rv_top_rated)
        topRatedRecyclerView.setHasFixedSize(true)
        topRatedRecyclerView.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        upcomingRecyclerView = view.findViewById(R.id.rv_upcoming)
        upcomingRecyclerView.setHasFixedSize(true)
        upcomingRecyclerView.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        popularRecyclerView = view.findViewById(R.id.rv_popular)
        popularRecyclerView.setHasFixedSize(true)
        popularRecyclerView.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        setupTrailerMoviesObserver()
        setupUpcomingObserver()
        setupTopRatedObserver()
        setupPopularObserver()
        setupSearchListener()

        val view_all_1: TextView = view.findViewById(R.id.txt_trailer_view)
        val view_all_2: TextView = view.findViewById(R.id.txt_popular_view)
        val view_all_3: TextView = view.findViewById(R.id.txt_top_view)
        val view_all_4: TextView = view.findViewById(R.id.txt_upcoming_view)

        view_all_1.setOnClickListener {
            val text_trailer_view: String = CATEGORY_TITLE_TRAILERS
            val bundle = bundleOf("text" to text_trailer_view)
            findNavController().navigate(R.id.action_fragmentHomeScreen_to_fragmentCategoryScreen, bundle)

        }
        view_all_2.setOnClickListener {
            val text_popular_view: String = CATEGORY_TITLE_POPULAR
            val bundle = bundleOf("text" to text_popular_view)
            findNavController().navigate(R.id.action_fragmentHomeScreen_to_fragmentCategoryScreen, bundle)
        }
        view_all_3.setOnClickListener {

            val text_top_view: String = CATEGORY_TITLE_TOP
            val bundle = bundleOf("text" to text_top_view)
            findNavController().navigate(R.id.action_fragmentHomeScreen_to_fragmentCategoryScreen, bundle)
        }
        view_all_4.setOnClickListener {
            val text_upcoming_view: String = CATEGORY_TITLE_UPCOMING
            val bundle = bundleOf("text" to text_upcoming_view)
            findNavController().navigate(R.id.action_fragmentHomeScreen_to_fragmentCategoryScreen, bundle)
        }

    }

    private fun setupSearchListener() =
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query == null || query.isEmpty()) return false

                val bundle = Bundle().apply {
                    putString("text", CATEGORY_TITLE_SEARCH)
                    putString(KEY_SEARCH_QUERY, query)
                }
                findNavController().navigate(R.id.fragmentCategoryScreen, bundle)

                return true
            }
            override fun onQueryTextChange(p0: String?): Boolean { return false }
        })

    private fun setupTrailerMoviesObserver() =
        homeVM.trailerMoviesLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    trailerRecyclerView.adapter = TrailerMoviesListAdapter(it.data, this)
                    trailerRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
                        // Category finishes loading when recycler finishes inflating items
                        markCategoryAsCompleted(Category.TrailerMovies)
                    }
                }
                is Resource.Error -> showError(it.message)
                is Resource.Loading -> markCategoryAsLoading(Category.TrailerMovies)
            }
        }

    private fun setupUpcomingObserver() =
        homeVM.upcomingLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    upcomingRecyclerView.adapter = MoviesListAdapter(it.data, this)
                    upcomingRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
                        markCategoryAsCompleted(Category.Upcoming)
                    }
                }
                is Resource.Error -> showError(it.message)
                is Resource.Loading -> markCategoryAsLoading(Category.Upcoming)
            }
        }

    private fun markCategoryAsLoading(category: Category) {
        loadingCategories.add(category)
        if(loadingCategories.size > 0) {
            loadingFrameLayout.isVisible = true
        }
    }

    private fun markCategoryAsCompleted(category: Category) {
        loadingCategories.remove(category)
        if(loadingCategories.size <= 0) {
            loadingFrameLayout.isVisible = false
        }
    }

    private fun setupTopRatedObserver() =
        homeVM.topRatedLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    topRatedRecyclerView.adapter = MoviesListAdapter(it.data, this)
                    topRatedRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
                        markCategoryAsCompleted(Category.TopRated)
                    }
                }
                is Resource.Error -> showError(it.message)
                is Resource.Loading -> markCategoryAsLoading(Category.TopRated)
            }
        }

    private fun setupPopularObserver() =
        homeVM.popularLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> {
                    setupMostPopular(it.data?.get(0))
                    val popularMoviesList = it.data?.filterIndexed { index, _ -> index > 0 }
                    popularRecyclerView.adapter = MoviesListAdapter(popularMoviesList, this)
                    popularRecyclerView.viewTreeObserver.addOnGlobalLayoutListener {
                        markCategoryAsCompleted(Category.Popular)
                    }
                }
                is Resource.Error -> showError(it.message)
                is Resource.Loading -> markCategoryAsLoading(Category.Popular)
            }
        }

    private fun setupMostPopular(movieNullable: Movie?) =
        movieNullable?.let { movie ->
            view?.let { rootView ->
                rootView.findViewById<TextView>(R.id.txt_title).text = movie.title
                rootView.findViewById<TextView>(R.id.txt_type).text = movie.genres.joinToString(", ")
                rootView.findViewById<TextView>(R.id.txt_length).text = getMovieTimeFormatted(movie.minutes)
                rootView.findViewById<Button>(R.id.btn_watch).setOnClickListener { startTrailerOfMovieId(movie.id) }
                setupMovieImage(movie.imageUrl, rootView.findViewById(R.id.iv_popular))
                setupRatingStars(rootView, movie.score)
                setupStartMovieDetailsListener(rootView.findViewById<CardView>(R.id.cv_popular), movie.id)
            }
        }

    private fun setupStartMovieDetailsListener(mostPopularView: View, movieId: Int) =
        mostPopularView.setOnClickListener {
            val movieDetailsIntent = Intent(requireActivity(), MoviesDetailsActivity::class.java)
            movieDetailsIntent.putExtra(MoviesDetailsActivity.KEY_MOVIE_ID, movieId)
            startActivity(movieDetailsIntent)
        }

    private fun getMovieTimeFormatted(minutes: Int): String {
        val hours = minutes / 60
        val extraMinutes = minutes % 60
        return "${hours}h${extraMinutes}m"
    }

    private fun setupMovieImage(imageUrl: String, imageView: ImageView) =
        activity?.let {
            Glide.with(it)
                .load(imageUrl)
                .into(imageView)
        }

    private fun setupRatingStars(rootView: View, rating: Float) {
        val ratingSingleDigit = floatToSingleDigitRoundedDown(rating)

        val fullStarsCount = ratingSingleDigit.toInt()
        val remainingFloatingPoint = ratingSingleDigit - fullStarsCount
        val isLastStarAtLeastHalf = remainingFloatingPoint > 0.01f // If at least .1 at the end, we add half a star
        val isLastStarFull = remainingFloatingPoint > 0.51f // If at least .6 at the end, we add a full star

        val starViews = getStarViews(rootView)

        for(i in 0 until fullStarsCount) {
            starViews[i].setImageResource(R.drawable.ic_full_star)
        }

        if(isLastStarFull) {
            starViews[fullStarsCount].setImageResource(R.drawable.ic_full_star)
        } else if (isLastStarAtLeastHalf) {
            starViews[fullStarsCount].setImageResource(R.drawable.ic_half_star)
        }
    }

    private fun getStarViews(rootView: View): List<ImageView> =
        rootView.run {
            listOf<ImageView>(
                findViewById(R.id.iv_star1),
                findViewById(R.id.iv_star2),
                findViewById(R.id.iv_star3),
                findViewById(R.id.iv_star4),
                findViewById(R.id.iv_star5)
            )
        }

    private fun floatToSingleDigitRoundedDown(number: Float): Float {
        val decimalFormat = DecimalFormat("#.#")
        decimalFormat.roundingMode = RoundingMode.FLOOR // If 4.59 -> 4.5; if 4.61 -> 4.6
        return decimalFormat.format(number).toFloat()
    }

    fun startTrailerOfMovieId(movieId: Int) =
        homeVM.getTrailerOfMovie(movieId).observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> gotoUrl(it.data?.trailerUrl ?: "")
                is Resource.Error -> showError(it.message, ERROR_LOADING_TRAILER_DEFAULT)
                is Resource.Loading -> {}
            }
        }

    private fun gotoUrl(url: String) {
        if(url.isEmpty()) return

        if(!hasInternet(requireContext())) {
            showInternetError(requireContext())
            return
        }

        if (url == TRAILER_ERROR_URL) {
            showError(ERROR_NO_TRAILER_FOUND)
            return
        }

        val uri: Uri = Uri.parse(url)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun showError(message: String?, fallbackError: String = ERROR_LOADING_MOVIE_DATA_DEFAULT) =
        Toast.makeText(context, message ?: fallbackError, Toast.LENGTH_LONG).show()

}


