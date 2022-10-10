package com.example.smartermovieapp.data.local.converter

import androidx.room.TypeConverter

class HoursConverter {

    companion object {
        private const val JOINING_SEPARATOR = "/"
    }

    @TypeConverter
    fun hoursListToString(hours: List<String>): String =
        hours.joinToString(JOINING_SEPARATOR)

    @TypeConverter
    fun stringToHoursList(string: String): List<String> =
        string.split(JOINING_SEPARATOR)

}