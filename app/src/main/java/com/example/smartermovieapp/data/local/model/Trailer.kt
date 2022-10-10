package com.example.smartermovieapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_trailer")
data class Trailer(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val movieId: Int,
    val trailerUrl: String
)