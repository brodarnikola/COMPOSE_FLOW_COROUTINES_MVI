package com.example.mvi_compose.general.utils

import android.location.Geocoder
import com.example.mvi_compose.general.di.IODispatcher
import com.example.mvi_compose.general.di.MovieNetwork
import com.example.mvi_compose.general.di.github.GithubNetwork
import com.example.mvi_compose.general.network.GithubApi
import com.example.mvi_compose.general.network.TrailerApi
import com.example.mvi_compose.general.repositories.MovieRepo
import com.example.mvi_compose.general.network.MovieApi
import com.example.mvi_compose.general.repositories.GithubRepo
import com.example.mvi_compose.general.repositories.GithubRepoImpl
import com.example.mvi_compose.general.repositories.LocationRepo
import com.example.mvi_compose.general.repositories.LocationRepoImpl
import com.example.mvi_compose.general.repositories.MovieRepoImpl
import com.example.mvi_compose.ui.settings.SharedFlowExample
import com.google.android.gms.location.FusedLocationProviderClient
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideMovieRepository(
        movieDao: MovieDao,
        @MovieNetwork moviesApi: MovieApi,
        @MovieNetwork trailersApi: TrailerApi,
        @MovieNetwork moshi: Moshi,
        @IODispatcher ioDispatcher: CoroutineDispatcher,
    ): MovieRepo {
        return MovieRepoImpl(movieDao, moviesApi, trailersApi, moshi, ioDispatcher)
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
    fun provideGithubRepository(@GithubNetwork githubApi: GithubApi, @GithubNetwork moshi: Moshi,
                                @IODispatcher ioDispatcher: CoroutineDispatcher,): GithubRepo {
        return GithubRepoImpl(githubApi, moshi, ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideLSharedFlowExample(@IODispatcher ioDispatcher: CoroutineDispatcher): SharedFlowExample {
        return SharedFlowExample(ioDispatcher)
    }
}