package com.example.smartermovieapp.presentation.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.presentation.movies_details.MoviesDetailsActivity
import java.math.RoundingMode
import java.text.DecimalFormat

class CategoryMoviesAdapter(
    private var movies: List<Movie>?,
    private val fragment: FragmentCategoryScreen
): RecyclerView.Adapter<CategoryMoviesAdapter.MovieHolder>() {

    inner class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txt_title)
        val typeTextView: TextView = itemView.findViewById(R.id.txt_type)
        val lengthTextView: TextView = itemView.findViewById(R.id.txt_length)
        val watchButton: Button = itemView.findViewById(R.id.btn_watch)
        val posterImageView: ImageView = itemView.findViewById(R.id.iv_popular)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_category, parent, false)
        return MovieHolder(view)
    }

    fun setMovies(movies: List<Movie>?) {
        this.movies = movies
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val movie = movies?.get(position) ?: return

        with(holder) {
            titleTextView.text = movie.title
            typeTextView.text = movie.genres.joinToString(", ")
            lengthTextView.text = getMovieTimeFormatted(movie.minutes)
            watchButton.setOnClickListener { startTrailerOfMovieId(movie.id) }
            setupMovieImage(movie.imageUrl, posterImageView)
            setupRatingStars(itemView, movie.score)
            setupStartMovieDetailsListener(holder.itemView, movie.id)
        }

    }

    private fun setupStartMovieDetailsListener(holderView: View, movieId: Int) =
        holderView.setOnClickListener {
            val movieDetailsIntent = Intent(fragment.requireActivity(), MoviesDetailsActivity::class.java)
            movieDetailsIntent.putExtra(MoviesDetailsActivity.KEY_MOVIE_ID, movieId)
            fragment.startActivity(movieDetailsIntent)
        }

    private fun startTrailerOfMovieId(id: Int) = fragment.startTrailerOfMovieId(id)

    private fun getMovieTimeFormatted(minutes: Int): String {
        val hours = minutes / 60
        val extraMinutes = minutes % 60
        return "${hours}h${extraMinutes}m"
    }

    private fun setupMovieImage(imageUrl: String, imageView: ImageView) =
        fragment.activity?.let {
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

    override fun getItemCount(): Int = movies?.size ?: 0
}