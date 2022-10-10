package com.example.smartermovieapp.data.local.converter

import androidx.room.TypeConverter
import com.example.smartermovieapp.data.local.model.Category

class CategoryConverter {

    @TypeConverter
    fun categoryToString(category: Category): String =
        category.name

    @TypeConverter
    fun stringToCategory(string: String): Category =
        enumValueOf(string)

}