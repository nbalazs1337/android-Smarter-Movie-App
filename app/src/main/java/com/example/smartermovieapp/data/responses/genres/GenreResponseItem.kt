package com.example.smartermovieapp.data.responses.genres


import com.google.gson.annotations.SerializedName

data class GenreResponseItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)