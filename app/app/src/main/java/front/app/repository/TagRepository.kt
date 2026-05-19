package front.app.repository

import front.app.model.Tag
import front.app.network.RetrofitClient

class TagRepository {
    private val api = RetrofitClient.instance

    suspend fun getTags(userId: Long) = api.getTags(userId)
    suspend fun saveTag(tag: Tag) = api.saveTag(tag)
}
