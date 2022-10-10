package com.example.smartermovieapp.data.responses.genres


import com.google.gson.annotations.SerializedName

data class GenresResponse(
    @SerializedName("genres")
    val genres: List<GenreResponseItem>
)