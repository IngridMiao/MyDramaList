package front.app.network

import front.app.model.Drama
import front.app.model.DramaResponse
import front.app.model.Tag
import front.app.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // User APIs
    @POST("api/users/login")
    suspend fun login(@Body user: User): Response<User>

    @POST("api/users")
    suspend fun createUser(@Body user: User): Response<User>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<User>

    @POST("api/users/{id}/friends")
    suspend fun addFriend(@Path("id") userId: Long, @Body request: front.app.model.FriendRequest): Response<Unit>

    @GET("api/users/{id}/friends")
    suspend fun getFriends(@Path("id") userId: Long): Response<List<User>>

    @GET("api/users/{id}/friend-requests")
    suspend fun getFriendRequests(@Path("id") userId: Long): Response<List<User>>

    @POST("api/users/{id}/friend-requests/{requesterId}/accept")
    suspend fun acceptFriendRequest(
        @Path("id") userId: Long,
        @Path("requesterId") requesterId: Long
    ): Response<Unit>

    @POST("api/users/{id}/friend-requests/{requesterId}/decline")
    suspend fun declineFriendRequest(
        @Path("id") userId: Long,
        @Path("requesterId") requesterId: Long
    ): Response<Unit>

    // Tag APIs
    @GET("api/tags")
    suspend fun getTags(@Query("userId") userId: Long): Response<List<Tag>>

    @POST("api/tags")
    suspend fun saveTag(@Body tag: Tag): Response<Tag>

    // Category APIs
    @GET("api/categories/{userId}")
    suspend fun getCategories(@Path("userId") userId: Long): Response<List<front.app.model.Category>>

    @POST("api/categories")
    suspend fun saveCategory(@Body category: front.app.model.Category): Response<front.app.model.Category>

    // Drama APIs
    @GET("api/dramas/public")
    suspend fun getPublicDramas(): Response<List<DramaResponse>>

    @GET("api/dramas/friends")
    suspend fun getFriendsDramas(@Query("userId") userId: Long): Response<List<DramaResponse>>

    @GET("api/dramas")
    suspend fun getDramas(
        @Query("userId") userId: Long,
        @Query("shown") shown: Boolean? = null
    ): Response<List<Drama>>

    @GET("api/dramas/{title}")
    suspend fun getDrama(
        @Path("title") title: String,
        @Query("userId") userId: Long
    ): Response<Drama>

    @POST("api/dramas")
    suspend fun saveDrama(@Body drama: Drama): Response<Drama>

    @DELETE("api/dramas/{title}")
    suspend fun deleteDrama(
        @Path("title") title: String,
        @Query("userId") userId: Long
    ): Response<Unit>
}
