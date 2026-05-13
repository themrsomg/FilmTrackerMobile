package com.example.santabarbaramobile.data.remote.reviews

import com.example.santabarbaramobile.data.model.CommentPaginationResponse
import com.example.santabarbaramobile.data.model.SingleCommentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CommentsApi {
    @GET("api/reviews/{reviewId}/comments")
    suspend fun getComments(
        @Header("Authorization") token: String?,
        @Path("reviewId") reviewId: String,
        @Query("page") page: Int = 1
    ): Response<CommentPaginationResponse>

    @Multipart
    @POST("api/reviews/{reviewId}/comments")
    suspend fun createComment(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: String,
        @Part("content") content: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<SingleCommentResponse>

    @DELETE("api/comments/{commentId}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: String
    ): Response<Unit>
}