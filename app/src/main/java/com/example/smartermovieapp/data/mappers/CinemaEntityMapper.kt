package com.example.smartermovieapp.data.mappers

import com.example.smartermovieapp.data.local.model.Cinema
import com.example.smartermovieapp.data.local.model.CinemaEntity
import com.example.smartermovieapp.data.local.model.Running
import com.example.smartermovieapp.data.local.model.RunningMovie
import com.example.smartermovieapp.data.local.model.relations.CinemaWithRunningMovies

fun CinemaWithRunningMovies.toCinema(): Cinema =
    cinemaEntity.toCinema(runningMovies)

fun CinemaEntity.toCinema(runningMovies: List<RunningMovie>): Cinema =
    Cinema(
        id = id,
        address = address,
        lat = lat,
        lng = lng,
        name = name,
        photo_url = photoUrl,
        running = runningMovies.map { it.toRunning() }
    )

private fun RunningMovie.toRunning(): Running =
    Running(
        movie_id = movieId,
        hours = hours
    )

fun Cinema.toCinemaEntity(): CinemaEntity =
    CinemaEntity(
        id = id,
        address = address,
        lat = lat,
        lng = lng,
        name = name,
        photoUrl = photo_url,
    )

fun Running.toRunningMovie(cinemaId: Int): RunningMovie =
    RunningMovie(
        cinemaId = cinemaId,
        movieId = movie_id,
        hours = hours
    )