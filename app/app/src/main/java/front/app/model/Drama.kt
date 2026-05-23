package front.app.model

data class Drama(
    val title: String,
    val userId: Long,
    val actors: String? = null,
    val tag: String? = null,
    val shown: Boolean = true,
    val grade: Float? = null,
    val viewPoint: String? = null,
    val link1: String? = null,
    val link2: String? = null,
    val link3: String? = null,
    val updatedAt: String? = null
)
