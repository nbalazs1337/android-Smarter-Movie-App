package com.example.smartermovieapp.data.local.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.smartermovieapp.data.local.converter.HoursConverter

@Entity(
    tableName = "table_running_movie",
    primaryKeys = ["cinemaId", "movieId"]
)
@TypeConverters(HoursConverter::class)
class RunningMovie (
    val cinemaId: Int,
    val movieId: Int,
    val hours: List<String>
)