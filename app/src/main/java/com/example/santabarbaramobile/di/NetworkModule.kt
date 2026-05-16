package com.example.santabarbaramobile.di

import android.util.Log
import com.example.santabarbaramobile.data.remote.SantaBarbaraApi
import com.example.santabarbaramobile.data.remote.auth.AuthApi
import com.example.santabarbaramobile.data.remote.library.UserLibraryApi
import com.example.santabarbaramobile.data.remote.users.UsersApi
import com.example.santabarbaramobile.data.remote.friends.FriendsApi
import com.example.santabarbaramobile.data.remote.moderation.ModerationApi
import com.example.santabarbaramobile.data.remote.notifications.NotificationsApi
import com.example.santabarbaramobile.data.remote.reviews.CommentsApi
import com.example.santabarbaramobile.data.remote.reviews.ReviewsApi
import com.example.santabarbaramobile.data.remote.reviews.LeaderboardsApi
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

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LibraryRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FriendsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ReviewsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NotificationsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ModerationRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val SHOWS_BASE_URL = "http://10.0.2.2:3001/"
    private const val USERS_BASE_URL = "http://10.0.2.2:3002/"
    private const val AUTH_BASE_URL = "http://10.0.2.2:3003/"
    private const val LIBRARY_BASE_URL = "http://10.0.2.2:3004/"
    private const val REVIEWS_BASE_URL = "http://10.0.2.2:3005/"
    private const val FRIENDS_BASE_URL = "http://10.0.2.2:3006/"
    private const val MODERATION_BASE_URL = "http://10.0.2.2:3007/"
    private const val NOTIFICATIONS_BASE_URL = "http://10.0.2.2:3008/"

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
    @LibraryRetrofit
    fun provideLibraryRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(LIBRARY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @ReviewsRetrofit
    fun provideReviewsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(REVIEWS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @FriendsRetrofit
    fun provideFriendsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FRIENDS_BASE_URL)
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
    fun provideUsersApi(@UsersRetrofit retrofit: Retrofit): UsersApi {
        return retrofit.create(UsersApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(@AuthRetrofit retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserLibraryApi(@LibraryRetrofit retrofit: Retrofit): UserLibraryApi {
        return retrofit.create(UserLibraryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReviewsApi(@ReviewsRetrofit retrofit: Retrofit): ReviewsApi {
        return retrofit.create(ReviewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFriendsApi(@FriendsRetrofit retrofit: Retrofit): FriendsApi {
        return retrofit.create(FriendsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCommentsApi(@ReviewsRetrofit retrofit: Retrofit): CommentsApi {
        return retrofit.create(CommentsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLeaderboardsApi(@ReviewsRetrofit retrofit: Retrofit): LeaderboardsApi {
        return retrofit.create(LeaderboardsApi::class.java)
    }

    @Provides
    @Singleton
    @NotificationsRetrofit
    fun provideNotificationsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NOTIFICATIONS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNotificationsApi(@NotificationsRetrofit retrofit: Retrofit): NotificationsApi {
        return retrofit.create(NotificationsApi::class.java)
    }

    @Provides
    @Singleton
    @ModerationRetrofit
    fun provideModerationRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(MODERATION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideModerationApi(@ModerationRetrofit retrofit: Retrofit): ModerationApi {
        return retrofit.create(ModerationApi::class.java)
    }
}