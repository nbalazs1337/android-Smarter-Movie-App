package com.example.smartermovieapp.presentation.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.presentation.cinema_details.CinemaDetailsActivity
import com.example.smartermovieapp.presentation.cinema_map.CheckLocationHandler
import com.example.smartermovieapp.presentation.cinema_map.CinemaMapActivity

class CategoryCinemasAdapter(
    private var cinemas: List<Cinema>?,
    private var fragment: Fragment
) : RecyclerView.Adapter<CategoryCinemasAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.txt_featured_cinema_name)
        val cinemaImageView: ImageView = itemView.findViewById(R.id.iv_featured_cinema)
        val cinemaCard: CardView = itemView.findViewById(R.id.cv_featured_cinema)
        val mapButton: Button = itemView.findViewById(R.id.btn_featured_cinema_map)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category_cinema, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cinema = cinemas?.get(position) ?: return

        with(holder) {
            nameText.text = cinema.name
            setupCinemaImage(cinema.photo_url, cinemaImageView)
            setupListener(cinemaCard, cinema.id)
            setupMapListener(mapButton, cinema)
        }
    }

    private fun setupMapListener(mapButton: Button, cinema: Cinema) =
        mapButton.setOnClickListener {
//            val intentUri = Uri.parse("geo:${cinema.lat},${cinema.lng}?q=${cinema.name}")
//            val mapIntent = Intent(Intent.ACTION_VIEW, intentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
//            fragment.startActivity(mapIntent)

            if(!CheckLocationHandler.hasLocationPermissions(fragment.requireContext())) {
                CheckLocationHandler.showEnablePermissionsMessage(fragment.requireContext())
                return@setOnClickListener
            }

            val cinemaMapIntent = Intent(fragment.requireActivity(), CinemaMapActivity::class.java).apply {
                putExtra(CinemaMapActivity.KEY_CINEMA_MAP_TYPE, CinemaMapActivity.TYPE_CINEMA)
                putExtra(CinemaMapActivity.KEY_CINEMA_ID_TO_SHOW, cinema.id)
            }
            fragment.startActivity(cinemaMapIntent)
        }

    private fun setupListener(cinemaCard: CardView, cinemaId: Int) =
        cinemaCard.setOnClickListener {
            val cinemaDetailsIntent = Intent(fragment.requireActivity(), CinemaDetailsActivity::class.java)
            cinemaDetailsIntent.putExtra(CinemaDetailsActivity.KEY_CINEMA_ID, cinemaId)
            fragment.startActivity(cinemaDetailsIntent)
        }

    private fun setupCinemaImage(imageUrl: String, imageView: ImageView) {
        fragment.activity?.let {
            Glide.with(it)
                .load(imageUrl)
                .into(imageView)
        }
    }

    override fun getItemCount(): Int = cinemas?.size ?: 0
}