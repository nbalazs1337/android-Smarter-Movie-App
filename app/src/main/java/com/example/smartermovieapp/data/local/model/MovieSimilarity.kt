package com.example.smartermovieapp.data.local.model

import androidx.room.Entity

@Entity(
    tableName = "table_similar_movies",
    primaryKeys = ["movieId", "similarMovieId"]
)
data class MovieSimilarity(
    val movieId: Int,
    val similarMovieId: Int
)