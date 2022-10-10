package com.example.smartermovieapp.data.mappers

import com.example.smartermovieapp.data.local.model.Trailer
import com.example.smartermovieapp.data.responses.trailer.TrailerResponse
import com.example.smartermovieapp.data.responses.trailer.TrailerResponseItem

const val TRAILER_TYPE = "Trailer"
const val BASE_TRAILER_URL = "https://www.youtube.com/watch?v="
const val TRAILER_ERROR_URL = "no_movie_found"

// The official trailer response has type 'Trailer'
// Return the last one to get the response of the oldest official trailer
fun TrailerResponse.toTrailer(): Trailer {
    if(results.isEmpty()) return Trailer(id.toString(), id, TRAILER_ERROR_URL)

    val lastTrailerTypeInResponse = results.lastOrNull { it.type == TRAILER_TYPE}
    val videoResponseItem = lastTrailerTypeInResponse ?: results.last()
    return videoResponseItem.toTrailer(id)
}

private fun TrailerResponseItem.toTrailer(movieId: Int) =
    Trailer(
        id = id,
        movieId = movieId,
        trailerUrl = BASE_TRAILER_URL + key
    )
