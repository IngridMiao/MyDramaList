package front.app

import android.app.Application
import front.app.network.RetrofitClient
import front.app.network.TmdbRetrofitClient

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
        // 先喚醒 Render 上休眠中的後端，縮短使用者第一次登入的等待時間
        RetrofitClient.warmUp()
        TmdbRetrofitClient.init(this)
    }
}
