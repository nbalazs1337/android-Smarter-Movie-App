package com.example.smartermovieapp.data.responses.trailer


import com.google.gson.annotations.SerializedName

data class TrailerResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<TrailerResponseItem>
)