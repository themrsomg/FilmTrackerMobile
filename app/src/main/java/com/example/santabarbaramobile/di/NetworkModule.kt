package com.example.santabarbaramobile.di

import com.example.santabarbaramobile.data.remote.SantaBarbaraApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // 10.0.2.2 apunta al localhost de ls pcerda desde el emulador
    private const val BASE_URL = "http://10.0.2.2:3001/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSantaBarbaraApi(retrofit: Retrofit): SantaBarbaraApi {
        return retrofit.create(SantaBarbaraApi::class.java)
    }
}