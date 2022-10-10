package com.example.smartermovieapp.data.repository

import android.net.Uri
import android.util.Log.d
import com.example.smartermovieapp.data.local.dao.*
import com.example.smartermovieapp.data.local.model.*
import com.example.smartermovieapp.data.mappers.*
import com.example.smartermovieapp.data.network.MovieApi
import com.example.smartermovieapp.data.responses.popular.PopularResponse
import com.example.smartermovieapp.data.responses.search.SearchResponse
import com.example.smartermovieapp.data.responses.similar_movies.SimilarMoviesResponse
import com.example.smartermovieapp.data.responses.top_rated.TopRatedResponse
import com.example.smartermovieapp.data.responses.up_coming.UpcomingResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class MoviesRepositoryImpl(
    private val movieApi: MovieApi,
    private val movieDao: MovieDao,
    private val trailerDao: TrailerDao,
    private val movieCategorizationDao: MovieCategorizationDao,
    private val movieSimilarityDao: MovieSimilarityDao,
    private val favouriteDao: FavouriteDao,
    private val cinemaDao: CinemaDao,
    private val runningMovieDao: RunningMovieDao
) : MoviesRepository {

    override fun getPopular(): Observable<List<Movie>> =
        movieApi.getPopular()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { popularResponse -> popularResponseToMovies(popularResponse) }
            .map { movies ->
                cacheMovies(movies)
                recacheCategorizations(Category.Popular, movies)
                movies.sortByDescending { it.popularity }
                movies
            }
            .onErrorResumeNext { movieDao.getMoviesOfCategory(Category.Popular) }

    private fun popularResponseToMovies(popularResponse: PopularResponse) =
        Observable.fromIterable(popularResponse.results)
            .flatMap { movieResponse ->
                movieApi.getDetailOfMovieId(movieResponse.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map { movieDetailsResponse ->
                        movieDetailsResponse.toMovie()
                    }
            }
            .toList()
            .toObservable()

    private fun cacheMovies(movies: List<Movie>) =
        movieDao.insertAllMovies(movies)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    val movieIds = movies.map { it.id }
                    cacheTrailersOfMovieIds(movieIds)
                    cacheSimilarMoviesOfMovieIds(movieIds)
                },
                { error -> error.printStackTrace() }
            )

    private fun cacheCategorizations(category: Category, movies: List<Movie>) =
        movieCategorizationDao.insertAllCategorizations(getBulkCategorizations(category, movies))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {},
                { error -> error.printStackTrace() }
            )

    private fun recacheCategorizations(category: Category, movies: List<Movie>) =
        movieCategorizationDao.deleteCategorizationsOfCategory(category)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .andThen {
                cacheCategorizations(category, movies)
            }
            .subscribe(
                {},
                { error -> error.printStackTrace() }
            )

    private fun getBulkCategorizations(category: Category, movies: List<Movie>) =
        movies.map { movie ->
            MovieCategorization(category, movie.id)
        }

    override fun getTopRated(): Observable<List<Movie>> =
        movieApi.getTopRated()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { topRatedResponse -> topRatedResponseToMovies(topRatedResponse) }
            .map { movies ->
                cacheMovies(movies)
                recacheCategorizations(Category.TopRated, movies)
                movies.sortByDescending { it.score }
                movies
            }
            .onErrorResumeNext { movieDao.getTopRatedMovies() }

    private fun topRatedResponseToMovies(topRatedResponse: TopRatedResponse) =
        Observable.fromIterable(topRatedResponse.results)
            .flatMap { movieResponse ->
                movieApi.getDetailOfMovieId(movieResponse.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map { movieDetailsResponse ->
                        movieDetailsResponse.toMovie()
                    }
            }
            .toList()
            .toObservable()

    override fun getUpcoming(): Observable<List<Movie>> =
        movieApi.getUpcoming()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { upcomingResponse -> upcomingResponseToMovies(upcomingResponse) }
            .map { movies ->
                cacheMovies(movies)
                recacheCategorizations(Category.Upcoming, movies)
                movies.sortByDescending { it.popularity }
                movies
            }
            .onErrorResumeNext { movieDao.getMoviesOfCategory(Category.Upcoming) }

    private fun upcomingResponseToMovies(upcomingResponse: UpcomingResponse) =
        Observable.fromIterable(upcomingResponse.results)
            .flatMap { movieResponse ->
                movieApi.getDetailOfMovieId(movieResponse.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map { movieDetailsResponse ->
                        movieDetailsResponse.toMovie()
                    }
            }
            .toList()
            .toObservable()

    override fun getTrailerMovies(): Observable<List<Movie>> =
        movieApi.getPopular()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { popularResponse -> popularResponseToTrailerMovies(popularResponse) }
            .map { movies ->
                recacheCategorizations(Category.TrailerMovies, movies)
                movies
            }
            .onErrorResumeNext { movieDao.getMoviesOfCategory(Category.TrailerMovies).map { it.shuffled() } }

    private fun popularResponseToTrailerMovies(popularResponse: PopularResponse) =
        Observable.fromIterable(popularResponse.results.shuffled())
            .flatMap { movieResponse ->
                movieApi.getDetailOfMovieId(movieResponse.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map { movieDetailsResponse ->
                        movieDetailsResponse.toMovie()
                    }
            }
            .toList()
            .toObservable()

    override fun getTrailerOfMovieId(movieId: Int): Observable<Trailer> =
        trailerDao.getTrailerOfMovieId(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())

    private fun cacheTrailerOfMovieId(movieId: Int) =
        movieApi.getTrailerOfMovieId(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { trailerResponse -> trailerResponse.toTrailer() }
            .subscribe(
                { result -> cacheTrailer(result) },
                { error -> error.printStackTrace() }
            )

    private fun cacheTrailersOfMovieIds(movieIds: List<Int>) =
        Observable.fromIterable(movieIds)
            .flatMap { id ->
                movieApi.getTrailerOfMovieId(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map { trailerResponse -> trailerResponse.toTrailer() }
            }
            .toList()
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { result -> cacheTrailers(result) },
                { error -> error.printStackTrace() }
            )

    private fun cacheTrailers(trailers: List<Trailer>) =
        trailerDao.insertAllTrailers(trailers)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {},
                { error -> error.printStackTrace() }
            )

    private fun cacheTrailer(trailer: Trailer) =
        trailerDao.insertTrailer(trailer)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {},
                { error -> error.printStackTrace() }
            )

    override fun getMovie(movieId: Int): Observable<Movie> =
        movieApi.getDetailOfMovieId(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { movieDetailsResponse ->
                val movie = movieDetailsResponse.toMovie()
                cacheMovie(movie)
                movie
            }
            .onErrorResumeNext { movieDao.getMovie(movieId) }

    private fun cacheMovie(movie: Movie) =
        movieDao.insertMovie(movie)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    cacheTrailerOfMovieId(movie.id)
                    cacheSimilarMovies(movie.id)
                },
                { error -> error.printStackTrace() }
            )

    override fun getPopularOnPage(page: Int): Observable<List<Movie>> =
        movieApi.getPopularOnPage(page)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { popularResponse -> popularResponseToMovies(popularResponse) }
            .map { movies ->
                cacheTrailersOfMovieIds(movies.map { it.id })
                movies.sortByDescending { it.popularity }
                movies
            }

    override fun getTopRatedOnPage(page: Int): Observable<List<Movie>> =
        movieApi.getTopRatedOnPage(page)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { topRatedResponse -> topRatedResponseToMovies(topRatedResponse) }
            .map { movies ->
                cacheTrailersOfMovieIds(movies.map { it.id })
                movies.sortByDescending { it.score }
                movies
            }

    override fun getUpcomingOnPage(page: Int): Observable<List<Movie>> =
        movieApi.getUpcomingOnPage(page)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { upcomingResponse -> upcomingResponseToMovies(upcomingResponse) }
            .map { movies ->
                cacheTrailersOfMovieIds(movies.map { it.id })
                movies.sortByDescending { it.popularity }
                movies
            }

    override fun getTrailerMoviesOnPage(page: Int): Observable<List<Movie>> =
        movieApi.getPopularOnPage(page)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { popularResponse -> popularResponseToTrailerMovies(popularResponse) }
            .map { movies ->
                cacheTrailersOfMovieIds(movies.map { it.id })
                movies
            }

    override fun getFavourites(): Observable<List<Favourite>> =
        favouriteDao.getAllFavourites()

    override fun insertFavourite(favourite: Favourite): Completable {
        return favouriteDao.insertFavourite(favourite)
    }

    override fun removeFavouriteMovie(movieId: Int): Completable {
        return favouriteDao.deleteFavourite(movieId)
    }

    override fun isFavourite(movieId: Int): Observable<Boolean> =
        favouriteDao.isFavourite(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())

    private fun cacheSimilarMovies(movieId: Int) =
        movieApi.getSimilarMovies(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe (
                { result ->
                    val similarMovieIds = result.results.map { it.id }
                    cacheSimilarities(movieId, similarMovieIds)
                },
                { error -> error.printStackTrace() }
            )

    private fun cacheSimilarMoviesOfMovieIds(movieIds: List<Int>) =
        Observable.fromIterable(movieIds)
            .map { cacheSimilarMovies(it) }
            .toList()
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {},
                { error -> error.printStackTrace() }
            )

    override fun getSimilarMovies(movieId: Int): Observable<List<Movie>> =
        movieApi.getSimilarMovies(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { similarMoviesResponse -> similarMoviesResponseToMovies(similarMoviesResponse) }
            .map { movies ->
                cacheMovies(movies)
                cacheSimilarities(movieId, movies.map { it.id } )
                movies.sortedByDescending { it.popularity }.filter { it.id != movieId }
            }
            .onErrorResumeNext { movieDao.getSimilarMovies(movieId) }

    private fun cacheSimilarities(movieId: Int, similarMovieIds: List<Int>) {
        val movieSimilarities = similarMovieIds.map { similarMovieId ->
            MovieSimilarity(movieId, similarMovieId)
        }
        movieSimilarityDao.insertAllSimilarities(movieSimilarities)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {},
                { error -> error.printStackTrace() }
            )
    }

    private fun similarMoviesResponseToMovies(similarMoviesResponse: SimilarMoviesResponse) =
        Observable.fromIterable(similarMoviesResponse.results)
            .flatMap { movieResponse ->
                movieApi.getDetailOfMovieId(movieResponse.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map{ movieDetailResponse ->
                        movieDetailResponse.toMovie()
                    }
            }
            .toList()
            .toObservable()

    private fun cacheFavourite(favourite: Favourite) =
        favouriteDao.insertFavourite(favourite)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {},
                { error -> error.printStackTrace() }
            )

    override fun getFavouriteMovies(): Observable<List<Movie>> =
        movieDao.getFavouriteMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())

    override fun getSearchFavouriteMovies(query: String): Observable<List<Movie>> =
        movieDao.getSearchFavouriteMovies(query)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())

    override fun getMoviesOfIds(movieIds: List<Int>): Observable<List<Movie>> =
        Observable.fromIterable(movieIds)
            .flatMap { movieId ->
                movieApi.getDetailOfMovieId(movieId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map{ movieDetailResponse ->
                        movieDetailResponse.toMovie()
                    }
            }
            .toList()
            .toObservable()
            .map { movies ->
                cacheMovies(movies)
                movies.sortByDescending { it.popularity }
                movies
            }
            .onErrorResumeNext { getMoviesOfIds(movieIds) }

    override fun getSearchMovies(query: String): Observable<List<Movie>> =
        movieApi.getSearchMovies(Uri.parse(query))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { searchResponse -> searchResponseToMovies(searchResponse) }
            .map { movies ->
                cacheMovies(movies)
                movies.sortedByDescending { it.popularity }
            }
            .onErrorResumeNext {
                movieDao.getSearchMovies(query)
                    .map { movies ->
                        movies.sortedByDescending { it.popularity }
                    } 
            }

    private fun searchResponseToMovies(searchResponse: SearchResponse): Observable<List<Movie>> =
        Observable.fromIterable(searchResponse.results)
            .flatMap { movieResponse ->
                movieApi.getDetailOfMovieId(movieResponse.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .map { movieDetailsResponse ->
                        movieDetailsResponse.toMovie()
                    }
            }
            .toList()
            .toObservable()

    override fun insertCinemas(cinemas: List<Cinema>): Completable =
        cinemaDao.insertAllCinemas(cinemas.map { it.toCinemaEntity() })
            .andThen {
                val runningMovies = mutableListOf<RunningMovie>()
                cinemas.forEach { cinema ->
                    val cinemaRunningMovies = cinema.running.map { it.toRunningMovie(cinema.id) }
                    runningMovies.addAll(cinemaRunningMovies)
                }
                runningMovieDao.insertAllRunningMovies(runningMovies)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {},
                        { error -> error.printStackTrace() }
                    )
            }
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())

    override fun getSearchMoviesOnPage(query: String, page: Int): Observable<List<Movie>> =
        movieApi.getSearchMoviesOnPage(Uri.parse(query), page)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .switchMap { searchResponse -> searchResponseToMovies(searchResponse) }
            .map { movies ->
                cacheTrailersOfMovieIds(movies.map { it.id })
                movies.sortedByDescending { it.popularity }
            }

    override fun getCinemas(): Observable<List<Cinema>> =
        cinemaDao.getCinemasWithRunningMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map {
                it.map { cinemaWithRunningMovies ->
                    cinemaWithRunningMovies.toCinema()
                }
            }

    override fun getCinema(cinemaId: Int): Observable<Cinema> =
        cinemaDao.getCinemaWithRunningMovies(cinemaId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { cinemaWithRunningMovies ->
                cinemaWithRunningMovies.toCinema()
            }

    override fun getCinemasRunningMovie(movieId: Int): Observable<List<Cinema>> =
        cinemaDao.getCinemasThatRunMovieAlongRunningMovies(movieId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map {
                it.map { cinemaWithRunningMovies ->
                    cinemaWithRunningMovies.toCinema()
                }
            }

    override fun getSearchCinemas(query: String): Observable<List<Cinema>> =
        cinemaDao.getSearchCinemasWithRunningMovies(query)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map {
                it.map { cinemaWithRunningMovies ->
                    cinemaWithRunningMovies.toCinema()
                }
            }

}