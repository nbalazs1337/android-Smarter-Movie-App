package com.example.smartermovieapp.data.responses.similar_movies

import com.google.gson.annotations.SerializedName

data class SimilarMoviesResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<SimilarMoviesResponseItem>,
    @SerializedName("total_pages")
    val total_pages: Int,
    @SerializedName("total_results")
    val total_results: Int
)