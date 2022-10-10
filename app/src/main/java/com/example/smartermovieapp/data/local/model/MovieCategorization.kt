package com.example.smartermovieapp.data.local.model

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.smartermovieapp.data.local.converter.CategoryConverter

@Entity(
    tableName = "table_category_and_movie",
    primaryKeys = ["category", "movieId"]
)
@TypeConverters(CategoryConverter::class)
data class MovieCategorization(
    val category: Category,
    val movieId: Int
)