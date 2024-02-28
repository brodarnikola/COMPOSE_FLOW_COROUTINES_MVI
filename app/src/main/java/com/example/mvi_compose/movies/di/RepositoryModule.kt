package com.example.mvi_compose.movies.utils

import android.location.Geocoder
import com.example.mvi_compose.movies.di.IODispatcher
import com.example.mvi_compose.movies.di.github.GithubNetwork
import com.example.mvi_compose.movies.network.GithubApi
import com.example.mvi_compose.movies.network.TrailerApi
import com.example.mvi_compose.movies.repositories.MovieRepo
import com.example.mvi_compose.movies.network.MovieApi
import com.example.mvi_compose.movies.repositories.GithubRepo
import com.example.mvi_compose.movies.repositories.GithubRepoImpl
import com.example.mvi_compose.movies.repositories.LocationRepo
import com.example.mvi_compose.movies.repositories.LocationRepoImpl
import com.example.mvi_compose.movies.repositories.MovieRepoImpl
import com.example.mvi_compose.ui.settings.SharedFlowExample
import com.google.android.gms.location.FusedLocationProviderClient
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
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
    fun provideLocationRepository(
        fusedLocationClient: FusedLocationProviderClient,
        geocoder: Geocoder,
    ): LocationRepo {
        return LocationRepoImpl(fusedLocationClient, geocoder)
    }

    @Singleton
    @Provides
    @GithubNetwork
    fun provideGithubRepository(@GithubNetwork githubApi: GithubApi, moshi: Moshi): GithubRepo {
        return GithubRepoImpl(githubApi, moshi)
    }

    @Singleton
    @Provides
    fun provideLSharedFlowExample(@IODispatcher ioDispatcher: CoroutineDispatcher): SharedFlowExample {
        return SharedFlowExample(ioDispatcher)
    }
}