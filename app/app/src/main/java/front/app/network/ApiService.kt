package front.app.network

import front.app.model.Drama
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

    // Drama APIs
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
