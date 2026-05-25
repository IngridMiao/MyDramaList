package front.app

import android.app.Application
import front.app.network.RetrofitClient
import front.app.network.TmdbRetrofitClient

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
        TmdbRetrofitClient.init(this)
    }
}
