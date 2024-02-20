package com.example.mvi_compose.movies.utils

import android.location.Geocoder
import com.example.mvi_compose.movies.network.GithubApi
import com.example.mvi_compose.movies.network.TrailerApi
import com.example.mvi_compose.movies.repositories.MovieRepo
import com.example.mvi_compose.movies.network.MovieApi
import com.example.mvi_compose.movies.repositories.GithubRepo
import com.example.mvi_compose.movies.repositories.GithubRepoImpl
import com.example.mvi_compose.movies.repositories.LocationRepo
import com.example.mvi_compose.movies.repositories.LocationRepoImpl
import com.example.mvi_compose.movies.repositories.MovieRepoImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideMovieRepository(
        movieDao: MovieDao,
        moviesApi: MovieApi,
        trailersApi: TrailerApi,
        moshi: Moshi
    ): MovieRepo {
        return MovieRepoImpl(movieDao, moviesApi, trailersApi, moshi)
    }

    @Singleton
    @Provides
    fun provideLocationRepository(fusedLocationClient: FusedLocationProviderClient, geocoder: Geocoder,) : LocationRepo {
        return LocationRepoImpl(fusedLocationClient, geocoder)
    }

    @Singleton
    @Provides
    fun provideGithubRepository(githubApi: GithubApi) : GithubRepo {
        return GithubRepoImpl(githubApi)
    }
}