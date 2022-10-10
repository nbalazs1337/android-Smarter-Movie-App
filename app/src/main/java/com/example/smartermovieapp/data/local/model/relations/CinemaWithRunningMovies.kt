package com.example.smartermovieapp.data.local.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.smartermovieapp.data.local.model.CinemaEntity
import com.example.smartermovieapp.data.local.model.RunningMovie

data class CinemaWithRunningMovies (
    @Embedded val cinemaEntity: CinemaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "cinemaId"
    )
    val runningMovies: List<RunningMovie>
)