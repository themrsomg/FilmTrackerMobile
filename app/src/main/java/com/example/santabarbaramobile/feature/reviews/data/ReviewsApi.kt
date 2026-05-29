package com.example.santabarbaramobile.feature.reviews.data

import com.example.santabarbaramobile.feature.reviews.domain.ReviewPaginationResponse
import com.example.santabarbaramobile.feature.reviews.domain.ReviewSummaryDto
import com.example.santabarbaramobile.feature.reviews.domain.SingleReviewResponse
import com.example.santabarbaramobile.feature.reviews.domain.UpdateReviewRequest
import com.example.santabarbaramobile.feature.profile.domain.AdminStatsResponse
import com.example.santabarbaramobile.feature.profile.domain.ReviewStatsDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ReviewsApi {

    @GET("api/reviews/show/{tvmazeId}")
    suspend fun getReviewsByShow(
        @Header("Authorization") token: String?,
        @Path("tvmazeId") tvmazeId: Int,
        @Query("page") page: Int = 1
    ): Response<ReviewPaginationResponse>

    @GET("api/reviews/user/{authId}")
    suspend fun getReviewsByUser(
        @Path("authId") authId: String,
        @Query("page") page: Int = 1
    ): Response<ReviewPaginationResponse>

    @GET("api/reviews/user/{authId}/summary")
    suspend fun getUserReviewsSummary(
        @Path("authId") authId: String
    ): Response<ReviewSummaryDto>

    @Multipart
    @POST("api/reviews")
    suspend fun createReview(
        @Header("Authorization") token: String,
        @Part("tvmazeId") tvmazeId: RequestBody,
        @Part("rating") rating: RequestBody,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<SingleReviewResponse>

    @POST("api/reviews/{reviewId}/like")
    suspend fun likeReview(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: String
    ): Response<Unit>

    @DELETE("api/reviews/{reviewId}/like")
    suspend fun unlikeReview(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: String
    ): Response<Unit>

    @DELETE("api/reviews/{reviewId}/image")
    suspend fun removeReviewImage(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: String
    ): Response<Unit>

    @Multipart
    @POST("api/reviews/{reviewId}/image")
    suspend fun uploadReviewImage(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: String,
        @Part image: MultipartBody.Part
    ): Response<SingleReviewResponse>

    @DELETE("api/reviews/{reviewId}")
    suspend fun deleteReview(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: String
    ): Response<Unit>

    @PUT("api/reviews/{reviewId}")
    suspend fun updateReview(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: String,
        @Body request: UpdateReviewRequest
    ): Response<SingleReviewResponse>

    @GET("api/reviews/admin/stats")
    suspend fun getReviewStats(
        @Header("Authorization") token: String
    ): Response<AdminStatsResponse<ReviewStatsDto>>

    @GET("api/reviews/{reviewId}")
    suspend fun getReviewById(
        @Header("Authorization") token: String?,
        @Path("reviewId") reviewId: String
    ): Response<SingleReviewResponse>

    @GET("api/reviews/user/{userId}")
    suspend fun getReviewsByUser(
        @Header("Authorization") token: String?,
        @Path("userId") userId: String,
        @Query("page") page: Int = 1
    ): Response<ReviewPaginationResponse>
}