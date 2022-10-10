package com.example.smartermovieapp.data.local.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.smartermovieapp.data.local.converter.CategoryConverter
import java.util.*

@Entity(
    tableName = "favourites",
    primaryKeys = ["movieId"]
)
@TypeConverters(CategoryConverter::class)
data class Favourite(
    val movieId: Int
)