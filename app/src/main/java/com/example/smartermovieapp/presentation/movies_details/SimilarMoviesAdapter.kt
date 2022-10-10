package com.example.smartermovieapp.presentation.movies_details

import android.graphics.drawable.Drawable
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
import com.example.smartermovieapp.utils.toSingleDecimalString


class SimilarMoviesAdapter(
    private val similarMovies: List<Movie>?,
    private val moviesDetailsActivity: MoviesDetailsActivity
) : RecyclerView.Adapter<SimilarMoviesAdapter.MovieHolder>() {

    inner class MovieHolder(val holderView: View): RecyclerView.ViewHolder(holderView) {
        val titleTextView: TextView = getTextViewOfId(R.id.tv_title)
        val ratingTextView: TextView = getTextViewOfId(R.id.tv_rating)

        val posterImageView: ImageView = holderView.findViewById(R.id.iv_poster)

        private fun getTextViewOfId(id: Int): TextView = holderView.findViewById(id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder =
        MovieHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_similar_movie, parent, false)
        )

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val movie = similarMovies?.get(position) ?: return

        with(holder) {
            titleTextView.text = getSpannableOfTitleAndYear(movie.title, movie.year)
            ratingTextView.text = movie.score.toSingleDecimalString()
            setupMovieImage(movie.imageUrl, posterImageView)
            setupStartMovieDetailsListener(holder.holderView, movie.id)

            if(position == similarMovies.size - 1) {
                extendMarginEnd()
            }
        }
    }

    private fun setupStartMovieDetailsListener(holderView: View, movieId: Int) =
        holderView.setOnClickListener {
            moviesDetailsActivity.startMovieDetailsScreenOfMovie(movieId)
        }

    private fun getSpannableOfTitleAndYear(title: String, year: Int): SpannableString {
        val yearToUse = if(year > 0) "($year)" else ""
        val text = "$title $yearToUse"
        val span = SpannableString(text)
        val yearColor = ContextCompat.getColor(moviesDetailsActivity, R.color.text_grey)

        span.setSpan(
            ForegroundColorSpan(yearColor),
            text.length - yearToUse.length,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return span
    }

    private fun MovieHolder.extendMarginEnd() {
        val holderView = this.holderView
        val newLayoutParams = LinearLayout.LayoutParams(holderView.layoutParams)
        val marginAsPixels = holderView.context.resources.getDimensionPixelSize(R.dimen.margin_movies_details_default)
        newLayoutParams.marginStart = marginAsPixels
        newLayoutParams.marginEnd = marginAsPixels
        holderView.layoutParams = newLayoutParams
    }

    private fun setupMovieImage(imageUrl: String, imageView: ImageView) =
        Glide.with(moviesDetailsActivity)
            .load(imageUrl)
            .error(R.drawable.no_image)
            .into(imageView)

    override fun getItemCount(): Int = similarMovies?.size ?: 0

}
