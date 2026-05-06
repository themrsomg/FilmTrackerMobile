package com.example.santabarbaramobile.di

import android.util.Log
import com.example.santabarbaramobile.data.remote.SantaBarbaraApi
import com.example.santabarbaramobile.data.remote.auth.AuthApi
import com.example.santabarbaramobile.data.remote.users.UsersApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ShowsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UsersRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val SHOWS_BASE_URL = "http://10.0.2.2:3001/"
    private const val AUTH_BASE_URL = "http://10.0.2.2:3003/"
    private const val USERS_BASE_URL = "http://10.0.2.2:3002/"


    @Provides
    @Singleton
    @UsersRetrofit
    fun provideUsersRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(USERS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUsersApi(@UsersRetrofit retrofit: Retrofit): UsersApi {
        return retrofit.create(UsersApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message ->
            Log.d("RetrofitTraffic", message)
        }
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    @ShowsRetrofit
    fun provideShowsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SHOWS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AUTH_BASE_URL)
            .client(okHttpClient)
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