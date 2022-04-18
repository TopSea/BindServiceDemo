package top.topsea.servicedemo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process.myPid
import android.widget.Toast

class AIDLService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        // Return the interface
        return binder
    }


    private val binder = object : IAidlServiceInterface.Stub() {
        override fun getName(): String {
            Toast.makeText(applicationContext, "hello! Android", Toast.LENGTH_SHORT).show()
            return "Android"
        }

        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String
        ) {
            // Does nothing
        }
    }
}