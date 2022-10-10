package com.example.smartermovieapp.presentation.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.presentation.cinema_details.CinemaDetailsActivity

class NearbyCinemasAdapter(
    private var cinemas: List<Cinema>?,
    private var fragment: Fragment
) : RecyclerView.Adapter<NearbyCinemasAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.txt_nearby_cinema_name)
        val cinemaImageView: ImageView = itemView.findViewById(R.id.img_nearby_cinema)
        val cinemaCard: CardView = itemView.findViewById(R.id.cv_cinema_nearby)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.nearby_cinema_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cinema = cinemas?.get(position) ?: return
        val size = cinemas?.size ?: return

        with(holder) {
            nameText.text = cinema.name
            setupCinemaImage(cinema.photo_url, cinemaImageView)
            setupListener(cinemaCard, cinema.id)

            if(position == size - 1) {
                extendMarginEnd()
            }
        }
    }

    private fun ViewHolder.extendMarginEnd() {
        val holderView = this.itemView
        val newLayoutParams = LinearLayout.LayoutParams(holderView.layoutParams)
        val marginAsPixels = holderView.context.resources.getDimensionPixelSize(R.dimen.margin_movies_details_default)
//        newLayoutParams.marginStart = marginAsPixels
        newLayoutParams.marginEnd = marginAsPixels
        holderView.layoutParams = newLayoutParams
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

    fun setCinemas(cinemas: List<Cinema>) {
        this.cinemas = cinemas
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = cinemas?.size ?: 0
}