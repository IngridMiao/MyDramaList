package front.app.network

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object TmdbRetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = "a715b17df421ef32d6670a97d7f3e82c" 

    private var okHttpClient: OkHttpClient? = null

    fun init(context: Context) {
        val cacheSize = 10 * 1024 * 1024L // 10 MB
        val cache = Cache(File(context.cacheDir, "tmdb_cache"), cacheSize)
        okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .build()
    }

    val instance: TmdbApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient ?: OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(TmdbApiService::class.java)
    }
}
