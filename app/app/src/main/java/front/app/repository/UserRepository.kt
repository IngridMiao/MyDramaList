package front.app.repository

import front.app.model.User
import front.app.network.RetrofitClient

class UserRepository {
    private val api = RetrofitClient.instance

    suspend fun login(user: User) = api.login(user)
    suspend fun createUser(user: User) = api.createUser(user)
    suspend fun getUser(id: Long) = api.getUser(id)
    suspend fun addFriend(userId: Long, friendUserName: String) = api.addFriend(userId, front.app.model.FriendRequest(friendUserName))
    suspend fun getFriends(userId: Long) = api.getFriends(userId)
    suspend fun getFriendRequests(userId: Long) = api.getFriendRequests(userId)
    suspend fun acceptFriendRequest(userId: Long, requesterId: Long) = api.acceptFriendRequest(userId, requesterId)
    suspend fun declineFriendRequest(userId: Long, requesterId: Long) = api.declineFriendRequest(userId, requesterId)
}
