package com.example.smartermovieapp.presentation.cinema_details

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.data.local.model.Running
import com.example.smartermovieapp.presentation.home.FragmentCategoryScreen
import com.example.smartermovieapp.presentation.movies_details.MoviesDetailsActivity
import java.math.RoundingMode
import java.text.DecimalFormat

class CinemaMoviesAdapter(
    private var movies: List<Movie>?,
    private var runningSpots: List<Running>,
    private val activity: CinemaDetailsActivity
): RecyclerView.Adapter<CinemaMoviesAdapter.MovieHolder>() {

    inner class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txt_title)
        val typeTextView: TextView = itemView.findViewById(R.id.txt_type)
        val lengthTextView: TextView = itemView.findViewById(R.id.txt_length)
        val posterImageView: ImageView = itemView.findViewById(R.id.iv_popular)
        val runningTexts: List<TextView> = listOf(
            itemView.findViewById(R.id.txt_running_time_1),
            itemView.findViewById(R.id.txt_running_time_2),
            itemView.findViewById(R.id.txt_running_time_3)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_running_movie, parent, false)
        return MovieHolder(view)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val movie = movies?.get(position) ?: return

        with(holder) {
            titleTextView.text = movie.title
            typeTextView.text = movie.genres.joinToString(", ")
            lengthTextView.text = getMovieTimeFormatted(movie.minutes)
            setupMovieImage(movie.imageUrl, posterImageView)
            setupRatingStars(itemView, movie.score)
            setupStartMovieDetailsListener(holder.itemView, movie.id)
            setupRunningSpots(movie.id, runningTexts)
        }

    }

    private fun setupRunningSpots(movieId: Int, runningTexts: List<TextView>) {
        val runningHours = runningSpots.find { it.movie_id == movieId } ?.hours ?: return
        runningTexts.forEachIndexed { index, textView ->
            textView.text = runningHours[index]
        }
    }

    private fun setupStartMovieDetailsListener(holderView: View, movieId: Int) =
        holderView.setOnClickListener {
            val movieDetailsIntent = Intent(activity, MoviesDetailsActivity::class.java)
            movieDetailsIntent.putExtra(MoviesDetailsActivity.KEY_MOVIE_ID, movieId)
            activity.startActivity(movieDetailsIntent)
        }

    private fun getMovieTimeFormatted(minutes: Int): String {
        val hours = minutes / 60
        val extraMinutes = minutes % 60
        return "${hours}h${extraMinutes}m"
    }

    private fun setupMovieImage(imageUrl: String, imageView: ImageView) =
        Glide.with(activity)
            .load(imageUrl)
            .into(imageView)

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