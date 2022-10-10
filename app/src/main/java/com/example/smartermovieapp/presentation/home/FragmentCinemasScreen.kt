package com.example.smartermovieapp.presentation.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.TransparentStatusBarHandler
import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.data.responses.Resource
import com.example.smartermovieapp.presentation.cinema_details.CinemaDetailsActivity
import com.example.smartermovieapp.presentation.cinema_map.CheckLocationHandler
import com.example.smartermovieapp.presentation.cinema_map.CinemaMapActivity
import com.example.smartermovieapp.presentation.cinema_map.CurrentLocationHandler
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class FragmentCinemasScreen : Fragment() {

    @Inject
    lateinit var cinemaVM: CinemaVM

    private lateinit var nearbyCinemasRecyclerView: RecyclerView
    private lateinit var adapter: NearbyCinemasAdapter

    companion object {
        const val ERROR_NO_CINEMAS_DEFAULT = "Could not load cinemas, please try again later."
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransparentStatusBarHandler.initTransparentStatusBar(requireActivity().window)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cinemas_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewAllText: TextView = view.findViewById(R.id.txt_view_all_cinemas)
        viewAllText.setOnClickListener {
            val textAllCinemas = "Cinemas"
            val bundle = bundleOf("text" to textAllCinemas)
            findNavController().navigate(
                R.id.action_fragmentCinemaScreen_to_fragmentCategoryScreen,
                bundle
            )
        }

        setupCinemasObserver(view)
        setupSearchListener(view)
    }

    private fun setupSearchListener(view: View) =
        view.findViewById<SearchView>(R.id.sv_search_cinema).setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query == null || query.isEmpty()) return false

                val bundle = Bundle().apply {
                    putString("text", CATEGORY_TITLE_SEARCH_CINEMA)
                    putString(KEY_SEARCH_QUERY, query)
                }
                findNavController().navigate(R.id.fragmentCategoryScreen, bundle)

                return true
            }
            override fun onQueryTextChange(p0: String?): Boolean = false
        })

    private fun setupCinemasObserver(view: View) =
        cinemaVM.cinemasLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Success -> it.data?.let { cinemas -> setupCinemasBasedOnLocation(cinemas, view) }
                is Resource.Error -> showError(it.message)
                is Resource.Loading -> {}
            }
        }

    private fun showError(message: String?, fallBackError: String = ERROR_NO_CINEMAS_DEFAULT) =
        Toast.makeText(requireContext(), message ?: fallBackError, Toast.LENGTH_LONG).show()

    private fun setupCinemasBasedOnLocation(cinemas: List<Cinema>, view: View) {
        if(CheckLocationHandler.isLocationAvailable(requireContext())) {
            CurrentLocationHandler.doOnCurrentLocation(requireActivity()) { latLngLocation ->
                val cinemasByLocation = cinemas.sortedBy { cinema ->
                    val cinemaLatLng = LatLng(cinema.lat, cinema.lng)
                    SphericalUtil.computeDistanceBetween(latLngLocation, cinemaLatLng)
                }
                setupCinemas(cinemasByLocation, view)
                cinemaVM.saveUserLocation(latLngLocation)
            }
        } else {
            val cinemasByLastLocation = cinemaVM.getCinemasByLastLocation(cinemas)
            setupCinemas(cinemasByLastLocation, view)
        }
    }

    private fun setupCinemas(cinemas: List<Cinema>, view: View) {
        val featuredCinema = cinemas[0]

        setupNearbyCinemasRecycler(cinemas.minus(featuredCinema), view)

        val featCinemaImg = view.findViewById<ImageView>(R.id.iv_featured_cinema)
        val featCinemaName = view.findViewById<TextView>(R.id.txt_featured_cinema_name)
        val featCinemaAddress = view.findViewById<TextView>(R.id.txt_featured_cinema_address)

        featCinemaName.text = featuredCinema.name
        featCinemaAddress.text = featuredCinema.address
        Glide.with(view)
            .load(featuredCinema.photo_url)
            .into(featCinemaImg)

        val lat = featuredCinema.lat
        val lng = featuredCinema.lng

        val featCinemaMapBtn = view.findViewById<AppCompatButton>(R.id.btn_featured_cinema_map)
        featCinemaMapBtn.setOnClickListener {

//            val intentUri = Uri.parse("geo:$lat,$lng?q=${featuredCinema.name}")
//            val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
//            startActivity(mapIntent)

            startCinemaMapWithCinema(featuredCinema)
        }


        val viewMapButton = view.findViewById<AppCompatButton>(R.id.btn_cinemas_map)
        viewMapButton.setOnClickListener {

//            val gmmIntentUri = Uri.parse("geo:0,0?q=cinemas")
//            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
//            startActivity(mapIntent)

            startCinemaMapWithAllCinemas()
        }

        setupFeaturedCinemaListener(view, featuredCinema.id)
    }

    private fun setupNearbyCinemasRecycler(cinemas: List<Cinema>, view: View) {
        nearbyCinemasRecyclerView = view.findViewById(R.id.rv_nearby_cinemas)
        nearbyCinemasRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = NearbyCinemasAdapter(cinemas, this)
        nearbyCinemasRecyclerView.adapter = adapter
    }

    private fun startCinemaMapWithCinema(cinema: Cinema) {
        if(!CheckLocationHandler.hasLocationPermissions(requireContext())) {
            CheckLocationHandler.showEnablePermissionsMessage(requireContext())
            return
        }

        val cinemaMapIntent = Intent(requireActivity(), CinemaMapActivity::class.java).apply {
            putExtra(CinemaMapActivity.KEY_CINEMA_MAP_TYPE, CinemaMapActivity.TYPE_CINEMA)
            putExtra(CinemaMapActivity.KEY_CINEMA_ID_TO_SHOW, cinema.id)
        }
        startActivity(cinemaMapIntent)
    }

    private fun startCinemaMapWithAllCinemas() {
        if(!CheckLocationHandler.hasLocationPermissions(requireContext())) {
            CheckLocationHandler.showEnablePermissionsMessage(requireContext())
            return
        }

        val cinemaMapIntent = Intent(requireActivity(), CinemaMapActivity::class.java).apply {
            putExtra(CinemaMapActivity.KEY_CINEMA_MAP_TYPE, CinemaMapActivity.TYPE_ALL_CINEMAS)
        }
        startActivity(cinemaMapIntent)
    }

    private fun setupFeaturedCinemaListener(view: View, featuredCinemaId: Int) =
        view.findViewById<CardView>(R.id.cv_featured_cinema).setOnClickListener{
            goToCinemaDetails(featuredCinemaId)
        }

    private fun goToCinemaDetails(cinemaId: Int) {
        val cinemaDetailsIntent = Intent(requireActivity(), CinemaDetailsActivity::class.java)
        cinemaDetailsIntent.putExtra(CinemaDetailsActivity.KEY_CINEMA_ID, cinemaId)
        startActivity(cinemaDetailsIntent)
    }

}
