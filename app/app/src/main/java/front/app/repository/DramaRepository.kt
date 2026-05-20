package front.app.repository

import front.app.model.Drama
import front.app.network.RetrofitClient

class DramaRepository {
    private val api = RetrofitClient.instance

    suspend fun getDramas(userId: Long, shown: Boolean? = null) = api.getDramas(userId, shown)
    suspend fun getPublicDramas() = api.getPublicDramas()
    suspend fun getDrama(title: String, userId: Long) = api.getDrama(title, userId)
    suspend fun saveDrama(drama: Drama) = api.saveDrama(drama)
    suspend fun deleteDrama(title: String, userId: Long) = api.deleteDrama(title, userId)
}
