package com.example.santabarbaramobile.core.network

import com.example.santabarbaramobile.feature.shows.domain.CastResponse
import com.example.santabarbaramobile.feature.shows.domain.EpisodeResponse
import com.example.santabarbaramobile.feature.shows.domain.GenreResponse
import com.example.santabarbaramobile.feature.shows.domain.HomeResponse
import com.example.santabarbaramobile.feature.shows.domain.SearchResultItem
import com.example.santabarbaramobile.feature.shows.domain.SeasonResponse
import com.example.santabarbaramobile.feature.shows.domain.Show
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SantaBarbaraApi {
    @GET("api/shows/home")
    suspend fun getHomeData(): HomeResponse

    @GET("api/shows/home")
    suspend fun getHome(): Response<HomeResponse>

    @GET("api/shows/{id}")
    suspend fun getShowById(@Path("id") showId: String): Response<Show>

    @GET("api/shows/{id}/seasons")
    suspend fun getShowSeasons(@Path("id") showId: String): Response<SeasonResponse>

    @GET("api/shows/{id}/episodes")
    suspend fun getShowEpisodes(@Path("id") showId: String): Response<EpisodeResponse>

    @GET("api/shows/{id}/cast")
    suspend fun getShowCast(@Path("id") showId: String): Response<CastResponse>

    @GET("api/shows/by-genre/{genre}")
    suspend fun getShowsByGenre(@Path("genre") genre: String): Response<GenreResponse>

    @GET("api/shows/search")
    suspend fun searchShows(@Query("q") query: String): Response<List<SearchResultItem>>
}