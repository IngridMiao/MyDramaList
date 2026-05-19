package front.app.repository

import front.app.model.User
import front.app.network.RetrofitClient

class UserRepository {
    private val api = RetrofitClient.instance

    suspend fun login(user: User) = api.login(user)
    suspend fun createUser(user: User) = api.createUser(user)
    suspend fun getUser(id: Long) = api.getUser(id)
}
