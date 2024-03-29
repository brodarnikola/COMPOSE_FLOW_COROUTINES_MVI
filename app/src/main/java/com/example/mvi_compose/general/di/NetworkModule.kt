package com.example.mvi_compose.general.utils

import com.example.mvi_compose.BuildConfig
import com.example.mvi_compose.general.di.MovieNetwork
import com.example.mvi_compose.general.network.MovieApi
import com.example.mvi_compose.general.network.TrailerApi
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
class NetworkModule {

    @Provides
    @Singleton
    @MovieNetwork
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor().also { it.setLevel(HttpLoggingInterceptor.Level.BODY) }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else OkHttpClient
        .Builder()
        .build()

    @Provides
    @Singleton
    @MovieNetwork
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(SerializeNulls.JSON_ADAPTER_FACTORY)
            .build()

    @Provides
    @Singleton
    @MovieNetwork
    fun provideRetrofit(
        @MovieNetwork okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(AppConstants.BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    @MovieNetwork
    fun provideMovieService( @MovieNetwork retrofit: Retrofit): MovieApi = retrofit.create(MovieApi::class.java)

    @Provides
    @Singleton
    @MovieNetwork
    fun provideTrailerService( @MovieNetwork retrofit: Retrofit): TrailerApi =
        retrofit.create(TrailerApi::class.java)

}

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class SerializeNulls() {
    companion object {
        val JSON_ADAPTER_FACTORY: JsonAdapter.Factory =
            object : JsonAdapter.Factory {
//                @Nullable
                override fun create(
                    type: Type,
                    annotations: Set<Annotation>,
                    moshi: Moshi
                ): JsonAdapter<*>? {
                    val nextAnnotations =
                        Types.nextAnnotations(annotations, SerializeNulls::class.java) ?: return null
                    return moshi.nextAdapter<Any>(this, type, nextAnnotations).serializeNulls()
                }
            }
    }
}