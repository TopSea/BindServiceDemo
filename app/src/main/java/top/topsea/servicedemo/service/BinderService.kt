package top.topsea.servicedemo.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*

class BinderService: Service() {
    private val binder = LocalBinder()

    private val mGenerator = Random()

    val randomNumber: Int
        get() = mGenerator.nextInt(100)

    inner class LocalBinder : Binder() {
        fun getService(): BinderService = this@BinderService
    }
    override fun onBind(p0: Intent?): IBinder {
        return binder
    }
}