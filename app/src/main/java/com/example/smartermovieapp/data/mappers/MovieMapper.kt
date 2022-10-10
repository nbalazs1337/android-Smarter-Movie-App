package com.example.smartermovieapp.data.mappers

import com.example.smartermovieapp.data.local.model.Movie
import com.example.smartermovieapp.data.responses.movie_details.MovieDetailsResponse
import com.example.smartermovieapp.data.responses.similar_movies.SimilarMoviesResponseItem

const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500"
const val NO_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/0/0a/No-image-available.png"

fun MovieDetailsResponse.toMovie(): Movie =
    Movie(
        id = id,
        title = title,
        overview = overview,
        language = originalLanguage,
        imageUrl = posterPath?.let { BASE_IMAGE_URL + posterPath } ?: NO_IMAGE_URL,
        score = voteAverage.toFloat() / 2f,
        popularity = popularity.toFloat(),
        year = getYearFromDateResponse(releaseDate),
        minutes = runtime,
        genres = genres.map { it.name }
    )

private fun getYearFromDateResponse(date: String): Int {
    if(date.isEmpty()) return 0
    return date.split("-")[0].toInt()
}

fun SimilarMoviesResponseItem.similarToMovie(): Movie =
    Movie(
        id = id,
        title = title,
        overview = overview,
        language = original_language,
        imageUrl = BASE_IMAGE_URL + backdrop_path,
        score = vote_average.toFloat() / 2f,
        popularity = popularity.toFloat(),
        year =  release_date.split("-")[0].toInt(),
        minutes = 0,
        genres = listOf()
    )