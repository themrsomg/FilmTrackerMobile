package com.example.santabarbaramobile.data.remote

import com.example.santabarbaramobile.data.model.HomeResponse
import com.example.santabarbaramobile.data.model.Season
import com.example.santabarbaramobile.data.model.Show
import com.example.santabarbaramobile.data.model.Episode
import com.example.santabarbaramobile.data.model.CastMember
import com.example.santabarbaramobile.data.model.CastResponse
import com.example.santabarbaramobile.data.model.EpisodeResponse
import com.example.santabarbaramobile.data.model.GenreResponse
import com.example.santabarbaramobile.data.model.SeasonResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response

interface SantaBarbaraApi {
    @GET("api/shows/home")
    suspend fun getHomeData(): HomeResponse

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
}