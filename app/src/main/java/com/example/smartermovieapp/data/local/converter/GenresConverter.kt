package com.example.smartermovieapp.data.local.converter

import androidx.room.TypeConverter

class GenresConverter {

    companion object {
        const val JOINING_SEPARATOR = "/"
    }

    @TypeConverter
    fun genresListToString(genres: List<String>): String =
        genres.joinToString(JOINING_SEPARATOR)

    @TypeConverter
    fun stringToGenresList(string: String): List<String> =
        string.split(JOINING_SEPARATOR)

}