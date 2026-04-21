package com.example.santabarbaramobile.di

import com.example.santabarbaramobile.data.remote.SantaBarbaraApi
import com.example.santabarbaramobile.data.remote.auth.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ShowsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val SHOWS_BASE_URL = "http://10.0.2.2:3001/"
    private const val AUTH_BASE_URL = "http://10.0.2.2:3003/"

    @Provides
    @Singleton
    @ShowsRetrofit
    fun provideShowsRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SHOWS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSantaBarbaraApi(@ShowsRetrofit retrofit: Retrofit): SantaBarbaraApi {
        return retrofit.create(SantaBarbaraApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(@AuthRetrofit retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}