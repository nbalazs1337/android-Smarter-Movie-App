package com.example.smartermovieapp.presentation.home

import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.presentation.movies_details.MoviesDetailsActivity

class TrailerMoviesListAdapter(
    private val trailerMovies: List<Movie>?,
    private val fragment: FragmentHomeScreen
) : RecyclerView.Adapter<TrailerMoviesListAdapter.MovieHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.items_trailers, parent, false)
        return MovieHolder(view)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val movie = trailerMovies?.get(position) ?: return

        with(holder) {
            titleTextView.text = getSpannableOfTitleAndYear(movie.title, movie.year)
            setupMovieImage(movie.imageUrl, posterImageView)
            setupPlayTrailerListener(holder.playImageView, movie.id)

            if(position == trailerMovies.size - 1) {
                extendMarginEnd()
            }
        }
    }

    private fun setupPlayTrailerListener(playButton: View, movieId: Int) =
        playButton.setOnClickListener {
            fragment.startTrailerOfMovieId(movieId)
        }

    private fun MovieHolder.extendMarginEnd() {
        val itemView = this.itemView
        val newLayoutParams = LinearLayout.LayoutParams(itemView.layoutParams)
        val horizontalMarginAsPixels = itemView.context.resources.getDimensionPixelSize(R.dimen.margin_movies_details_default)
        val topMarginAsPixels = itemView.context.resources.getDimensionPixelSize(R.dimen.margin_movie_card_top)
        newLayoutParams.marginStart = horizontalMarginAsPixels
        newLayoutParams.marginEnd = horizontalMarginAsPixels
        newLayoutParams.topMargin = topMarginAsPixels
        itemView.layoutParams = newLayoutParams
    }

    private fun setupMovieImage(imageUrl: String, imageView: ImageView) =
        fragment.activity?.let {
            Glide.with(it)
                .load(imageUrl)
                .error(R.drawable.no_image)
                .into(imageView)
        }

    private fun getSpannableOfTitleAndYear(title: String, year: Int): SpannableString {
        val yearToUse = if(year > 0) "($year)" else ""
        val text = "$title $yearToUse"
        val span = SpannableString(text)
        val yearColor = fragment.activity?.let { ContextCompat.getColor(it, R.color.text_grey) } ?: Color.WHITE

        span.setSpan(
            ForegroundColorSpan(yearColor),
            text.length - yearToUse.length,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return span
    }

    override fun getItemCount(): Int = trailerMovies?.size ?: 0

    inner class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        val posterImageView: ImageView = itemView.findViewById(R.id.iv_item_trailer)
        val playImageView: ImageView = itemView.findViewById(R.id.iv_play)
    }

}