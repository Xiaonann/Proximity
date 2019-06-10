package com.example.proximity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.ContextCompat
import android.util.Log
import com.estimote.internal_plugins_api.scanning.BluetoothScanner
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.estimote.scanning_plugin.api.EstimoteBluetoothScannerFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //private lateinit var bluetoothScanner: BluetoothScanner

    private lateinit var mySocketService :TryServerService
    private lateinit var myStickerService :Sticker
    private var boundService = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // for bind server socket way1 can also written in onStart
        Intent(this, TryServerService::class.java).also { intent ->
            bindService(intent, serveSocketConnection, Context.BIND_AUTO_CREATE)
        }
        // bind sticker service
        Intent(this, Sticker::class.java).also { intent ->
            bindService(intent, stickerConnection, Context.BIND_AUTO_CREATE)
        }


        //Requirements check for estimote
        RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
            this,
            onRequirementsFulfilled = {
                Log.d("Beacons","onRequirementsFulfilled")

                // start proximity service
                val proximityServiceIntent = Intent(this,ProximityService::class.java)
                ContextCompat.startForegroundService(this,proximityServiceIntent)

            },
            onRequirementsMissing = {},
            onError = {}
        )
        bt_pepper.setOnClickListener {

           // mySocketService.receive?.let { tv_pepper.text = it}
            //Log.d("Msg from client", "${myService.receive}")
            //myStickerService.startSticker()?.let { tv_pepper.text = it }
            //Log.d("Msg from client", "${myStickerService.startSticker()}")

        }



    }


    //way 2
    private fun bindServeSocket(){

    }
    /**bind to the server service can define a fun then run it on onCreate() or other places
    or start intent and bindService on onCreate() then define a val = object:ServiceConnection
    then override fun onServiceConnected
     */

    // way 1 after that can use myService to get all PUBLIC methods in TryServerService
    /** Defines callbacks for service binding, passed to bindService()  */
    private val serveSocketConnection = object : ServiceConnection {
        private val tag = this.javaClass.name
        // the client use IBinder to communicate with the bound service.
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            //Log.d("bindservice","success")
            // We've bound to TryServerService, cast the IBinder and get TryServerService instance
            val socketBinder = service as TryServerService.TryServerBinder
            mySocketService = socketBinder.getService()
            boundService = true
            mySocketService.initSocket()

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            boundService = false
        }
    }

    // for bind sticker service

    private val stickerConnection = object : ServiceConnection {
        // the client use IBinder to communicate with the bound service.
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            //Log.d("bindservice","success")
            // We've bound to TryServerService, cast the IBinder and get TryServerService instance
            val stickerBinder = service as Sticker.StickerBinder
            myStickerService = stickerBinder.getService()
            val test = myStickerService.startSticker()
            Log.d("main","$test")
            boundService = true

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            boundService = false
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        unbindService(serveSocketConnection)
        unbindService(stickerConnection)
        //if implement the onStartCommand() callback method, must explicitly stop the service,
        val proximityServiceIntent = Intent(this,ProximityService::class.java)
        stopService(proximityServiceIntent)
        //Log.d("main","proximityDestory")
        //val intent = Intent(this, TryServerService::class.java)
        //stopService(intent)


    }


}
