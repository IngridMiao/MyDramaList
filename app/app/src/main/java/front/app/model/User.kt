package front.app.model

data class User(
    val id: Long? = null,
    val userName: String,
    val password: String? = null,
    val birth: String? = null
)
