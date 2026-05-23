package front.app.model

import com.google.gson.annotations.SerializedName

data class TmdbSearchResponse(
    val results: List<TmdbSearchResult>
)

data class TmdbSearchResult(
    val id: Int,
    @SerializedName("media_type")
    val mediaType: String?, // "tv" or "movie"
    val name: String?, // for TV
    val title: String?, // for Movie
    @SerializedName("first_air_date")
    val firstAirDate: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("poster_path")
    val posterPath: String?
) {
    val displayTitle: String get() = title ?: name ?: ""
}

data class TmdbCreditsResponse(
    val cast: List<TmdbCast>
)

data class TmdbCast(
    val name: String
)

data class DramaSuggestion(
    val title: String,
    val actors: List<String>,
    val tmdbId: Int,
    val posterPath: String?
)
