package com.example.smartermovieapp.data.local.model

data class Cinema(
    val id: Int,
    val address: String,
    val lat: Double,
    val lng: Double,
    val name: String,
    val photo_url: String,
    val running: List<Running>
)