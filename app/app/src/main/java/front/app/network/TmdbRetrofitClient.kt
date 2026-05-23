package front.app.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TmdbRetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    // TODO: 使用者應在此處填入自己的 TMDB API Key
    const val API_KEY = "a715b17df421ef32d6670a97d7f3e82c" 

    val instance: TmdbApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(TmdbApiService::class.java)
    }
}
