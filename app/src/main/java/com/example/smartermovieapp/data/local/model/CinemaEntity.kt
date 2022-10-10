package com.example.smartermovieapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_cinema")
data class CinemaEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val address: String,
    val lat: Double,
    val lng: Double,
    val name: String,
    val photoUrl: String
)