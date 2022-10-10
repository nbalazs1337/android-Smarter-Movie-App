package com.example.smartermovieapp.data.responses.up_coming


import com.google.gson.annotations.SerializedName

data class UpcomingResponseDates(
    @SerializedName("maximum")
    val maximum: String,
    @SerializedName("minimum")
    val minimum: String
)