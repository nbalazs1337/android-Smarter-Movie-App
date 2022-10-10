package com.example.smartermovieapp.data.responses.search

import com.example.smartermovieapp.data.responses.popular.PopularResponseItem
import com.google.gson.annotations.SerializedName

data class SearchResponse (
    val page: Int,
    val results: List<SearchResponseItem>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)