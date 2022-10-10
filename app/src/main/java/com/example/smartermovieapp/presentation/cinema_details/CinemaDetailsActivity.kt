package com.example.smartermovieapp.presentation.cinema_details

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.data.local.model.Running
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.TransparentStatusBarHandler
import com.example.smartermovieapp.presentation.cinema_map.CheckLocationHandler
import com.example.smartermovieapp.presentation.cinema_map.CinemaMapActivity
import com.example.smartermovieapp.presentation.home.CategoryVM
import com.example.smartermovieapp.presentation.home.CinemaVM
import com.example.smartermovieapp.utils.observeOnce
import dagger.android.AndroidInjection
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CinemaDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var cinemaVM: CinemaVM

    private lateinit var mapButton: ImageView
    private lateinit var loadingImage: ImageView

    companion object {
        const val KEY_CINEMA_ID = "key_cinema_id"

        const val ERROR_NO_CINEMAS_DEFAULT = "Could not load cinemas, please try again later."
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema_details)
        TransparentStatusBarHandler.initTransparentStatusBar(window)

        mapButton = findViewById(R.id.btn_map)
        loadingImage = findViewById(R.id.iv_loading)

        val cinemaId = intent.extras?.getInt(KEY_CINEMA_ID) ?: 1
        setupCinemasObserver(cinemaId)

        setupPreviousListener()
    }

    private fun setupCinemasObserver(cinemaId: Int) =
        cinemaVM.getCinemaLiveData(cinemaId).observeOnce(this) {
            when(it) {
                is Resource.Success ->
                    it.data?.let { cinema ->
                        setupScheduledMoviesObserver(cinema)
                        setupCinemaDetails(cinema)
                        setupMapListener(cinema)
                    }
                is Resource.Error -> showError(it.message)
                is Resource.Loading -> {}
            }
        }

    private fun showError(message: String?, fallBackError: String = ERROR_NO_CINEMAS_DEFAULT) =
        Toast.makeText(this, message ?: fallBackError, Toast.LENGTH_LONG).show()

    private fun setupCinemaDetails(cinema: Cinema) {
        findViewById<TextView>(R.id.txt_cinema_title).text = cinema.name
        findViewById<TextView>(R.id.txt_address).text = cinema.address
        setupCinemaPoster(findViewById(R.id.iv_poster_image), cinema.photo_url)
        setupDate(findViewById(R.id.txt_date))
    }

    private fun setupDate(dateText: TextView) {
        val date = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("EEEE dd.MM.yyyy", Locale.getDefault())
        val dateString = dateFormat.format(date)
        dateText.text = dateString
    }

    private fun setupCinemaPoster(poster: ImageView, imageUrl: String) =
        Glide.with(this)
            .load(imageUrl)
            .into(poster)

    private fun setupScheduledMoviesObserver(cinema: Cinema) {
        val runningMovies = cinema.running
        val movieIds = runningMovies.map { it.movie_id }
        cinemaVM.getMoviesLiveDataOfIds(movieIds).observeOnce(this) {
            when(it) {
                is Resource.Success -> {
                    setupScheduledMoviesRecycler(it.data, runningMovies)
                    loadingImage.isVisible = false
                }
                is Resource.Error -> showError(it.message ?: CategoryVM.ERROR_COULD_NOT_LOAD_MOVIES_DEFAULT)
                is Resource.Loading -> loadingImage.isVisible = true
            }
        }
    }

    private fun showError(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun setupScheduledMoviesRecycler(movies: List<Movie>?, runningMovies: List<Running>) =
        findViewById<RecyclerView>(R.id.rv_similar_movies).run {
            adapter = CinemaMoviesAdapter(movies, runningMovies, this@CinemaDetailsActivity)
            layoutManager = LinearLayoutManager(context)
        }

    private fun setupPreviousListener() =
        findViewById<ImageButton>(R.id.btn_previous).setOnClickListener {
            this@CinemaDetailsActivity.finish()
        }

    private fun setupMapListener(cinema: Cinema) =
        findViewById<ImageButton>(R.id.btn_map).setOnClickListener {
//            val intentUri = Uri.parse("geo:${cinema.lat},${cinema.lng}?q=${cinema.name}")
//            val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
//            startActivity(mapIntent)

            if(!CheckLocationHandler.hasLocationPermissions(this)) {
                CheckLocationHandler.showEnablePermissionsMessage(this)
                return@setOnClickListener
            }

            val cinemaMapIntent = Intent(this, CinemaMapActivity::class.java).apply {
                putExtra(CinemaMapActivity.KEY_CINEMA_MAP_TYPE, CinemaMapActivity.TYPE_CINEMA)
                putExtra(CinemaMapActivity.KEY_CINEMA_ID_TO_SHOW, cinema.id)
            }
            startActivity(cinemaMapIntent)
        }
}
