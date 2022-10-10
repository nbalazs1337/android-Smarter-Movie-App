package com.example.smartermovieapp.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartermovieapp.R
import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.presentation.home.FragmentFavoritesScreen
import com.example.smartermovieapp.presentation.movies_details.MoviesDetailsActivity
import com.google.android.material.snackbar.Snackbar
import java.math.RoundingMode
import java.text.DecimalFormat

class FavouriteMovieAdapter(
    private var favouriteMovies: List<Movie>?,
    private val fragment: FragmentFavoritesScreen
) : RecyclerView.Adapter<FavouriteMovieAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textGenres: TextView = itemView.findViewById(R.id.txt_type)
        val textName: TextView = itemView.findViewById(R.id.txt_title)
        val textLength: TextView = itemView.findViewById(R.id.txt_length)
        val buttonWatch: Button = itemView.findViewById(R.id.btn_watch)
        val posterImage: ImageView = itemView.findViewById(R.id.iv_popular)
        val motionLayout: MotionLayout = itemView.findViewById(R.id.ml_card_swipe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_favourite, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = favouriteMovies?.get(position) ?: return

        with(holder){
            textName.text = movie.title
            textGenres.text = movie.genres.joinToString(", ")
            textLength.text = getMovieTimeFormatted(movie.minutes)
            buttonWatch.setOnClickListener { startTrailerOfMovieId(movie.id) }
            setupMovieImage(movie.imageUrl, posterImage)
            setupRatingStars(itemView, movie.score)
            setupDeleteListener(motionLayout, movie)
            setupForceOnClickListener(motionLayout)
        }

        holder.setIsRecyclable(false)
    }

    companion object {
        const val TIME_MOVED_THRESHOLD_TO_REGISTER_SWIPE = 150L
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupForceOnClickListener(motionLayout: MotionLayout) {
        // Motion layout on a recycler view can't run both onClick and onSwipe transitions for the same view
        // So we force the click transition by listening to OnTouch motion events
        // If we register a swipe, we don't interfere - otherwise, we force the motion layout to click
        var isSwipe = false
        // Most of the times, a tap on the screen will also register as ACTION_MOVE
        // So we measure the amount of time spent in ACTION_MOVE to differentiate it between a simple tap and a swipe
        var timeMoved = 0L
        var timeMovedStart = 0L
        motionLayout.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isSwipe = false
                    timeMoved = 0L
                    timeMovedStart = System.currentTimeMillis()

                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    if(timeMoved > TIME_MOVED_THRESHOLD_TO_REGISTER_SWIPE) {
                        isSwipe = true
                        return@setOnTouchListener false
                    }

                    val currentTimeMillis = System.currentTimeMillis()
                    timeMoved += currentTimeMillis - timeMovedStart
                    timeMovedStart = currentTimeMillis

                    false
                }
                MotionEvent.ACTION_UP -> {
                    if (isSwipe) return@setOnTouchListener false

                    motionLayout.setTransition(R.id.transition_click)
                    motionLayout.transitionToEnd()

                    view.performClick()
                    return@setOnTouchListener true
                }
                else -> { false }
            }
        }
    }

    private fun setupDeleteListener(swipeMotionLayout: MotionLayout, movie: Movie) =
        swipeMotionLayout.addTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if(currentId == R.id.cs_end_click) {
                    startMovieDetails(movie.id)
                    return
                }
                if(currentId != R.id.cs_end_delete) return

                fragment.unmarkFavourite(movie.id)
                val snack = Snackbar.make(swipeMotionLayout,"Deleting movie ...", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", View.OnClickListener {
                        fragment.markFavourite(movie.id)
                        val snack2 = Snackbar.make(it, "Movie added back", Snackbar.LENGTH_LONG)
                        snack2.show()
                    }).setActionTextColor(Color.parseColor("#01feff"))
                snack.show()
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })

    private fun startMovieDetails(movieId: Int) {
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

    private fun floatToSingleDigitRoundedDown(number: kotlin.Float): kotlin.Float {
        val decimalFormat = DecimalFormat("#.#")
        decimalFormat.roundingMode = RoundingMode.FLOOR // If 4.59 -> 4.5; if 4.61 -> 4.6
        return decimalFormat.format(number).toFloat()
    }

    fun setFavouriteMovies(favouriteMovies: List<Movie>?) {
        this.favouriteMovies = favouriteMovies
        notifyDataSetChanged()
    }

    //counts how many items I have in the recycleView
    override fun getItemCount(): Int = favouriteMovies?.size ?: 0
}