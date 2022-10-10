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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.presentation.movies_details.MoviesDetailsActivity
import com.example.smartermovieapp.utils.toSingleDecimalString

class MoviesListAdapter(
    private val movies: List<Movie>?,
    private val fragment: FragmentHomeScreen
): RecyclerView.Adapter<MoviesListAdapter.MovieHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_card, parent, false)
        return MovieHolder(view)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val movie = movies?.get(position) ?: return

        with(holder) {
            titleTextView.text = getSpannableOfTitleAndYear(movie.title, movie.year)
            ratingTextView.text = movie.score.toSingleDecimalString()
            setupMovieImage(movie.imageUrl, posterImageView)
            setupStartMovieDetailsListener(holder.itemView, movie.id)

            if(position == movies.size - 1) {
                extendMarginEnd()
            }
        }
    }

    private fun setupStartMovieDetailsListener(holderView: View, movieId: Int) =
        holderView.setOnClickListener {
            val movieDetailsIntent = Intent(fragment.requireActivity(), MoviesDetailsActivity::class.java)
            movieDetailsIntent.putExtra(MoviesDetailsActivity.KEY_MOVIE_ID, movieId)
            fragment.startActivity(movieDetailsIntent)
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

    override fun getItemCount(): Int = movies?.size ?: 0

    inner class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        val ratingTextView: TextView = itemView.findViewById(R.id.tv_rating)
        val posterImageView: ImageView = itemView.findViewById(R.id.iv_poster)
    }
}