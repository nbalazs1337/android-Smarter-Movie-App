package com.example.smartermovieapp.presentation.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.data.mappers.TRAILER_ERROR_URL
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.TransparentStatusBarHandler
import com.example.smartermovieapp.presentation.cinema_map.CheckLocationHandler
import com.example.smartermovieapp.presentation.cinema_map.CurrentLocationHandler
import com.example.smartermovieapp.utils.hasInternet
import com.example.smartermovieapp.utils.observeOnce
import com.example.smartermovieapp.utils.showInternetError
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

const val CATEGORY_TITLE_TOP = "Top"
const val CATEGORY_TITLE_POPULAR = "Popular"
const val CATEGORY_TITLE_TRAILERS = "Trailers"
const val CATEGORY_TITLE_UPCOMING = "Upcoming"
const val CATEGORY_TITLE_FAVORITE = "Search Favorite"
const val CATEGORY_TITLE_SEARCH = "Search"
const val CATEGORY_TITLE_SEARCH_CINEMA = "Search Cinema"
const val CATEGORY_TITLE_AVAILABILITY = "Movie Availability"

const val KEY_SEARCH_QUERY = "search_query"
const val KEY_MOVIE_ID_AVAILABILITY = "movie_id_availability"

class FragmentCategoryScreen : Fragment() {

    @Inject
    lateinit var categoryVM: CategoryVM

    @Inject
    lateinit var cinemaVM: CinemaVM

    private lateinit var categoryText: String
    private lateinit var myRecyclerView: RecyclerView
    private lateinit var adapter: CategoryMoviesAdapter
    private lateinit var loadingImage: ImageView
    private lateinit var noElementsText: TextView
    private lateinit var loadingText: TextView
    private var pagesLoaded: Int = 0
    private var searchQuery: String = ""

    private lateinit var cinemasAdapter: CategoryCinemasAdapter

    companion object {
        const val ERROR_NO_TRAILER_FOUND = "No trailer found for this movie."
        const val ERROR_NO_CINEMAS_DEFAULT = "Could not load cinemas, please try again later."
        const val PROMPT_NO_CINEMA_FOUND = "No cinema found"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransparentStatusBarHandler.initTransparentStatusBar(requireActivity().window)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as NavigationBarVisibilityHandler).hideNavigationBarForFragmentCreation()

        return inflater.inflate(R.layout.fragment_category_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val text_category: TextView = view.findViewById(R.id.txt_category)
        if (arguments?.getString("text") != null) {
            categoryText = arguments?.getString("text") ?: CATEGORY_TITLE_POPULAR
            if (categoryText == "Cinemas" || categoryText == CATEGORY_TITLE_AVAILABILITY || categoryText == CATEGORY_TITLE_SEARCH_CINEMA)
                text_category.text = "$categoryText"
            else
                text_category.text = "$categoryText Movies"
        }

        loadingImage = view.findViewById(R.id.iv_loading)
        noElementsText = view.findViewById(R.id.txt_no_elements)
        loadingText = view.findViewById(R.id.txt_loading)

        myRecyclerView = view.findViewById(R.id.rv_category)
        myRecyclerView.layoutManager = LinearLayoutManager(context)

        adapter = CategoryMoviesAdapter(listOf(), this)
        cinemasAdapter = CategoryCinemasAdapter(listOf(), this)
        myRecyclerView.adapter = adapter

        if (categoryText != "Cinemas" && categoryText != CATEGORY_TITLE_AVAILABILITY && categoryText != CATEGORY_TITLE_SEARCH_CINEMA) {
            loadCategoryMovies()
            setupCategoryMoviesObserver()
            setupLoadMoviesOnScrollEndListener()
            setupIsLoadingMoreMoviesObserver()
        } else {
            setupCinemasObserver()
        }
    }

    private fun setupIsLoadingMoreMoviesObserver() {
        if(categoryText == CATEGORY_TITLE_FAVORITE) return

        categoryVM.isLoadingMoreMoviesLiveData.observe(viewLifecycleOwner) {
            loadingImage.isVisible = it
        }
    }

    private fun loadCategoryMovies() {
        if(categoryText == CATEGORY_TITLE_FAVORITE) {
            loadSearchFavoriteMovies()
            return
        }

        if(categoryText == CATEGORY_TITLE_SEARCH) {
            loadSearchMovies()
            pagesLoaded++
            return
        }

        categoryVM.loadCategoryMovies(categoryText)
        pagesLoaded++
    }

    private fun loadSearchMovies() {
        searchQuery = arguments?.getString(KEY_SEARCH_QUERY) ?: ""

        if(searchQuery.isEmpty()) return

        categoryVM.loadSearchMovies(searchQuery)
    }

    private fun setupCinemasObserver() =
        getCinemasToObserve().observeOnce(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> it.data?.let { cinemas ->
                    if(cinemas.isEmpty()) {
                        noElementsText.text = PROMPT_NO_CINEMA_FOUND
                        noElementsText.isVisible = true
                        return@let
                    }
                    loadCinemasBasedOnLocation(cinemas)
                }
                is Resource.Error -> showError(it.message, ERROR_NO_CINEMAS_DEFAULT)
                is Resource.Loading -> {}
            }
        }

    private fun loadCinemasBasedOnLocation(cinemas: List<Cinema>) {
        if(CheckLocationHandler.isLocationAvailable(requireContext())) {
            CurrentLocationHandler.doOnCurrentLocation(requireActivity()) { latLngLocation ->
                val cinemasOrdered = cinemas.sortedBy { cinema ->
                    val cinemaLatLng = LatLng(cinema.lat, cinema.lng)
                    SphericalUtil.computeDistanceBetween(latLngLocation, cinemaLatLng)
                }
                loadCinemas(cinemasOrdered)
                cinemaVM.saveUserLocation(latLngLocation)
            }
        } else {
            val cinemasByLastLocation = cinemaVM.getCinemasByLastLocation(cinemas)
            loadCinemas(cinemasByLastLocation)
        }
    }

    private fun getCinemasToObserve(): MutableLiveData<Resource<List<Cinema>>> {
        if(categoryText == CATEGORY_TITLE_AVAILABILITY) {
            val movieId = arguments?.getInt(KEY_MOVIE_ID_AVAILABILITY) ?: -1
            return cinemaVM.getCinemasRunningMovieLiveData(movieId)
        }

        if(categoryText == CATEGORY_TITLE_SEARCH_CINEMA) {
            searchQuery = arguments?.getString(KEY_SEARCH_QUERY) ?: ""
            return cinemaVM.getSearchCinemasLiveData(searchQuery)
        }

        return cinemaVM.cinemasLiveData
    }

    private fun loadCinemas(cinemas: List<Cinema>) {
        cinemasAdapter = CategoryCinemasAdapter(cinemas, this)
        myRecyclerView.adapter = cinemasAdapter
    }

    private fun loadSearchFavoriteMovies() {
        searchQuery = arguments?.getString(KEY_SEARCH_QUERY) ?: ""

        if(searchQuery.isEmpty()) return

        categoryVM.loadSearchFavoriteMovies(searchQuery)
    }

    private fun setupLoadMoviesOnScrollEndListener() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        if (categoryText == CATEGORY_TITLE_FAVORITE) return

        val activity = requireActivity() as HomeActivity
        activity.findViewById<ScrollView>(R.id.scrollView2)
            .setOnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
                val scrollView = v as ScrollView
                val lastChild = scrollView.getChildAt(scrollView.childCount - 1)
                val isLastChildVisible = lastChild != null
                val hasScrolledToEnd = scrollY >= lastChild.measuredHeight - scrollView.measuredHeight
                val isCurrentlyLoading = loadingImage.isVisible

                if (isLastChildVisible && hasScrolledToEnd && scrollY >= oldScrollY && !isCurrentlyLoading) {
                    pagesLoaded++

                    if(categoryText == CATEGORY_TITLE_SEARCH) {
                        if(searchQuery.isEmpty()) return@setOnScrollChangeListener
                        categoryVM.loadMoreSearchMoviesOnPage(searchQuery, pagesLoaded)
                        return@setOnScrollChangeListener
                    }

                    categoryVM.loadMoreCategoryMoviesFromPage(categoryText, pagesLoaded)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as NavigationBarVisibilityHandler).showNavigationBarForFragmentDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    fun startTrailerOfMovieId(movieId: Int) =
        categoryVM.getTrailerOfMovie(movieId).observeOnce(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> gotoUrl(it.data?.trailerUrl ?: "")
                is Resource.Error -> showError(
                    it.message,
                    FragmentHomeScreen.ERROR_LOADING_TRAILER_DEFAULT
                )
                is Resource.Loading -> {}
            }
        }

    private fun gotoUrl(url: String) {
        if (url.isEmpty()) return

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

    private fun setupCategoryMoviesObserver() =
        categoryVM.categoryMoviesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    adapter.setMovies(it.data)
                    loadingText.isVisible = false
                    if(it.data == null || it.data.isEmpty()) {
                        noElementsText.isVisible = true
                    }
                }
                is Resource.Error -> {
                    showError(it.message)
                    loadingText.isVisible = false
                }
                is Resource.Loading -> {
                    if(adapter.itemCount == 0)
                        loadingText.isVisible = true
                }
            }
        }

    private fun showError(
        message: String?,
        fallbackError: String = FragmentHomeScreen.ERROR_LOADING_MOVIE_DATA_DEFAULT
    ) =
        Toast.makeText(context, message ?: fallbackError, Toast.LENGTH_LONG).show()

}