package front.app.network

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object RetrofitClient {
    private val BASE_URL: String = if (isEmulator()) {
        "http://10.0.2.2:8080/"
    } else {
        "http://10.43.144.128:8080/"
    }

    private fun isEmulator(): Boolean {
        return android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || "google_sdk" == android.os.Build.PRODUCT
    }

    private var okHttpClient: OkHttpClient? = null

    fun init(context: Context) {
        val cacheSize = 10 * 1024 * 1024L // 10 MB
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)
        okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .build()
    }

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient ?: OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}
