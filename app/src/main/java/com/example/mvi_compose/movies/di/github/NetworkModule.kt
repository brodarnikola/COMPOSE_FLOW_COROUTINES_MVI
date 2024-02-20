package com.example.mvi_compose.movies.utils

import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.movies.di.github.GithubNetwork
import com.example.mvi_compose.movies.network.GithubApi
import com.example.mvi_compose.movies.network.TrailerApi
import com.example.mvi_compose.movies.network.MovieApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.Date
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class GithubNetworkModule {

    @Provides
    @Singleton
    @GithubNetwork
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor().apply { level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE }

    @Provides
    @Singleton
    @GithubNetwork
    fun provideAuthInterceptorOkHttpClient( @GithubNetwork interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor)
            .build()
    }


    @Provides
    @Singleton
    @GithubNetwork
    fun provideGsonConverterFactory( @GithubNetwork gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    @GithubNetwork
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .create()
    }

    @Singleton
    @Provides
    @GithubNetwork
    fun provideRetrofit(@GithubNetwork converterFactory: GsonConverterFactory, @GithubNetwork client: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(AppConstants.BASE_URL_GITHUB)
            .addConverterFactory(converterFactory)
    }

    @Singleton
    @Provides
    @GithubNetwork
    fun provideGithubRestApiService( @GithubNetwork retrofit: Retrofit.Builder): GithubApi {
        return retrofit
            .build()
            .create(GithubApi::class.java)
    }

}