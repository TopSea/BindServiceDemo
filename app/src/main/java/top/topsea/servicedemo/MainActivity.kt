package top.topsea.servicedemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import top.topsea.servicedemo.service.AIDLService
import top.topsea.servicedemo.service.BinderService
import top.topsea.servicedemo.service.IAidlServiceInterface
import top.topsea.servicedemo.service.MessengerService

class MainActivity : AppCompatActivity() {
    private var aidlService: IAidlServiceInterface? = null
    private var aidlBound: Boolean = false
    private lateinit var binderService: BinderService
    private var bindBound: Boolean = false
    private var messengerService: Messenger? = null
    private var messengerBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val bindConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as BinderService.LocalBinder
            binderService = binder.getService()
            bindBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bindBound = false
        }
    }

    private val messengerConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            messengerService = Messenger(service)
            messengerBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            messengerService = null
            messengerBound = false
        }
    }

    private val aidlConnection =object : ServiceConnection {
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            aidlService = IAidlServiceInterface.Stub.asInterface(service)
            aidlBound = true
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            aidlService = null
            aidlBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binderServiceBtn = findViewById<Button>(R.id.binder_service)
        binderServiceBtn.setOnClickListener { _ ->
            if (bindBound) {
                // Call a method from the LocalService.
                // However, if this call were something that might hang, then this request should
                // occur in a separate thread to avoid slowing down the activity performance.
                val num: Int = binderService.randomNumber
                Toast.makeText(this, "number: $num", Toast.LENGTH_SHORT).show()
            }
        }

        val messengerServiceBtn = findViewById<Button>(R.id.messenger_service)
        messengerServiceBtn.setOnClickListener { _ ->
            if (messengerBound) {
                val msg: Message = Message.obtain(null, 0, 3, 0)
                try {
                    messengerService?.send(msg)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        val aidlServiceBtn = findViewById<Button>(R.id.aidl_service)
        aidlServiceBtn.setOnClickListener { _ ->
            if (messengerBound) {
                val num = aidlService!!.name
                Toast.makeText(this, "number: $num", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, BinderService::class.java).also { intent ->
            bindService(intent, bindConnection, Context.BIND_AUTO_CREATE)
        }
        Intent(this, MessengerService::class.java).also { intent ->
            bindService(intent, messengerConnection, Context.BIND_AUTO_CREATE)
        }
        Intent(this, AIDLService::class.java).also { intent ->
            bindService(intent, aidlConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        // Unbind from the service
        if (bindBound) {
            unbindService(bindConnection)
            bindBound = false
        }
        if (messengerBound) {
            unbindService(messengerConnection)
            messengerBound = false
        }
        if (aidlBound) {
            unbindService(aidlConnection)
            aidlBound = false
        }
    }
}