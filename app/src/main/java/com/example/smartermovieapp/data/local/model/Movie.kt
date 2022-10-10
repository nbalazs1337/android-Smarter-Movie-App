package com.example.smartermovieapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.smartermovieapp.data.local.converter.GenresConverter

@Entity(tableName = "table_movie")
@TypeConverters(GenresConverter::class)
data class Movie(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val title: String,
    val overview: String,
    val language: String,
    val imageUrl: String,
    val score: Float,
    val popularity: Float,
    val year: Int,
    val minutes: Int,
    val genres: List<String>
)