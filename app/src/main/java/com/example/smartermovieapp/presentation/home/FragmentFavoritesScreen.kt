package com.example.smartermovieapp.presentation.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.mappers.TRAILER_ERROR_URL
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.FavouriteMovieAdapter
import com.example.smartermovieapp.utils.hasInternet
import com.example.smartermovieapp.utils.showInternetError
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FragmentFavoritesScreen : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    @Inject
    lateinit var favouriteVM: FavouriteVM

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var adapter: FavouriteMovieAdapter
    private lateinit var searchView: SearchView

    val recyclerView: RecyclerView by lazy {
        requireView().findViewById(R.id.rv_favorites)
    }

    companion object {
        const val ERROR_NO_TRAILER_FOUND = "No trailer found for this movie."
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = view.findViewById(R.id.sv_search_movie)

        adapter = FavouriteMovieAdapter(listOf(), this)
        myRecyclerView = view.findViewById(R.id.rv_favorites)
        myRecyclerView.adapter = adapter
        myRecyclerView.layoutManager = LinearLayoutManager(context)

        setupFavouriteMoviesListener()
        setupSearchListener()
    }

    private fun setupSearchListener() =
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query == null || query.isEmpty()) return false

                val bundle = Bundle().apply {
                    putString("text", CATEGORY_TITLE_FAVORITE)
                    putString(KEY_SEARCH_QUERY, query)
                }
                findNavController().navigate(R.id.fragmentCategoryScreen, bundle)

                return true
            }
            override fun onQueryTextChange(p0: String?): Boolean { return false }
        })

    private fun setupFavouriteMoviesListener() =
        favouriteVM.favouriteMoviesLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> adapter.setFavouriteMovies(it.data)
                is Resource.Error -> showError(it.message ?: FavouriteVM.ERROR_FAVOURITE_MOVIES_DEFAULT)
                is Resource.Loading -> {}
            }
        }

    private fun showError(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    fun markFavourite(movieId: Int) = favouriteVM.markFavorite(movieId)

    fun unmarkFavourite(movieId: Int) = favouriteVM.unmarkFavourite(movieId)

    fun startTrailerOfMovieId(movieId: Int) =
        favouriteVM.getTrailerOfMovie(movieId).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> gotoUrl(it.data?.trailerUrl ?: "")
                is Resource.Error -> showError(it.message ?: FavouriteVM.ERROR_COULD_NOT_LOAD_TRAILER_DEFAULT)
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

}