package front.app.network

import android.content.Context
import android.util.Log
import front.app.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val TAG = "RetrofitClient"

    /**
     * 後端網址決定順序：
     * 1. 有設定 BACKEND_URL（local.properties 或 GitHub Secret）→ 一律使用，模擬器也一樣
     * 2. 沒設定 → 視為本機開發，模擬器連到電腦的 localhost
     *
     * 以前是「先判斷是不是模擬器」，在部分實機上 isEmulator() 會誤判（例如 PRODUCT 名稱含 "sdk"），
     * 導致實機連到 10.0.2.2 而一直 timeout。
     */
    val BASE_URL: String = run {
        val configured = BuildConfig.BACKEND_URL.trim()
        val url = when {
            configured.isNotEmpty() -> configured
            isGenymotion() -> "http://10.0.3.2:8080/"
            else -> "http://10.0.2.2:8080/"
        }
        // Retrofit 要求 baseUrl 結尾一定要有 "/"
        if (url.endsWith("/")) url else "$url/"
    }

    private fun isGenymotion(): Boolean =
        android.os.Build.MANUFACTURER.contains("Genymotion") ||
                android.os.Build.PRODUCT.contains("vbox86p")

    private var okHttpClient: OkHttpClient? = null

    /**
     * Render 免費方案閒置 15 分鐘後會休眠，第一次請求需要約 30～90 秒冷啟動。
     * OkHttp 預設只等 10 秒，所以會直接丟出 SocketTimeoutException（畫面顯示 "timeout"）。
     * 這裡把逾時拉長，並在收到 Render 喚醒中的 502/503/504 時自動重試。
     */
    private fun buildClient(cache: Cache?): OkHttpClient =
        OkHttpClient.Builder()
            .apply { if (cache != null) cache(cache) }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(150, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(WakeUpRetryInterceptor())
            .build()

    fun init(context: Context) {
        val cacheSize = 10 * 1024 * 1024L // 10 MB
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)
        okHttpClient = buildClient(cache)
    }

    /**
     * App 一啟動就先戳一下後端，讓 Render 開始開機。
     * 使用者輸入帳密的這段時間，伺服器就差不多醒了。
     */
    fun warmUp() {
        val client = okHttpClient ?: buildClient(null)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(BASE_URL + "actuator/health").get().build()
                client.newCall(request).execute().close()
                Log.d(TAG, "Backend warm-up done")
            } catch (e: Exception) {
                Log.d(TAG, "Backend warm-up failed: ${e.message}")
            }
        }
    }

    val instance: ApiService by lazy {
        Log.d(TAG, "BASE_URL = $BASE_URL")
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient ?: buildClient(null))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    /** Render 冷啟動期間會回 502/503/504，或直接連線失敗；每 5 秒重試一次，最多 20 次。 */
    private class WakeUpRetryInterceptor(
        private val maxRetries: Int = 20,
        private val delayMs: Long = 5_000
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var attempt = 0
            while (true) {
                try {
                    val response = chain.proceed(request)
                    if (response.code in setOf(502, 503, 504) && attempt < maxRetries) {
                        response.close()
                        attempt++
                        Log.d(TAG, "Backend waking up (HTTP ${response.code}), retry $attempt")
                        Thread.sleep(delayMs)
                        continue
                    }
                    return response
                } catch (e: IOException) {
                    // 已被取消（例如使用者離開畫面或 callTimeout 到了）就不要再重試。
                    // POST 只在「根本沒連上」時重試，避免請求其實已送達卻重送（例如重複註冊）。
                    val safeToRetry = request.method == "GET" || e is java.net.ConnectException
                    if (chain.call().isCanceled() || !safeToRetry || attempt >= maxRetries) throw e
                    attempt++
                    Log.d(TAG, "Backend not reachable (${e.message}), retry $attempt")
                    Thread.sleep(delayMs)
                }
            }
        }
    }
}
