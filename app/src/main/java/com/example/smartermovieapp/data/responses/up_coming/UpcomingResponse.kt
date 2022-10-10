package com.example.smartermovieapp.data.responses.up_coming


import com.google.gson.annotations.SerializedName

data class UpcomingResponse(
    @SerializedName("dates")
    val dates: UpcomingResponseDates,
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<UpcomingResponseItem>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)