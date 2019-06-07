package com.example.proximity

/**
 *  using service for proximity(bluetooth need to always running)
 *  first without worker thread later can add
 *
 */

import android.app.*
import android.content.*
import android.widget.Toast
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials
import com.estimote.proximity_sdk.api.ProximityObserver
import com.estimote.proximity_sdk.api.ProximityObserverBuilder
import java.util.*
import kotlin.concurrent.schedule

private const val TAG = "ProximityService"
class ProximityService: Service(), BeaconUtils.BeaconListener {

    private var mObservationHandler: ProximityObserver.Handler? = null
    // enter 0 exit 1 for sliding window (or should from outside)
    private var stateSign: Int? = null
    private var stateArray = ArrayList<Int>()
    //test for connect with server service
    var proximityResult :String? = null
    //val timer = Timer()
    //private lateinit var notification: Notification

    // Cloud credentials found from https://cloud.estimote.com/
    private val cloudCredentials =
        EstimoteCloudCredentials("laboratorium-dibris-gmail--kfg", "90e1b9d8344624e9c2cd42b9f5fd6392")

    //for bind

    private lateinit var myService :TryServerService
    private var binder: TryServerService.TryServerBinder? = null
    private var boundService = false

    /**bind to the server service can define a fun then run it on onCreate() or other places
    or start intent and bindService on onCreate() then define a val = object:ServiceConnection
    then override fun onServiceConnected
     */

    // way 1 after that can use myService to get all PUBLIC methods in TryServerService
    /** Defines callbacks for service binding, passed to bindService()  */
    private val serveSocketConnection = object : ServiceConnection {
        //private val tag = this.javaClass.name
        // the client use IBinder to communicate with the bound service.
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.d("bindservice","success")
            // We've bound to TryServerService, cast the IBinder and get TryServerService instance
            binder = service as TryServerService.TryServerBinder
            boundService = true

        }


        override fun onServiceDisconnected(arg0: ComponentName) {
            boundService = false
        }
    }


    override fun onCreate() {
        super.onCreate()
        BeaconUtils.listener = this
        //notification = NotificationCreator().createNotification(this)
        Log.d(TAG,"create")
        //bind to server socket service and write the result to it
        Intent(this, TryServerService::class.java).also { intent ->
            bindService(intent, serveSocketConnection, Context.BIND_AUTO_CREATE)
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val proximityObserver = ProximityObserverBuilder(applicationContext, cloudCredentials)
            .withBalancedPowerMode()
            .withAnalyticsReportingDisabled()
            .withTelemetryReportingDisabled()
            .onError { throwable: Throwable -> Log.d("Beacons", throwable.toString()) }
            .build()
        mObservationHandler = proximityObserver.startObserving(BeaconUtils.beaconZones)


        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mObservationHandler?.stop()
        unbindService(serveSocketConnection)

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onEnterZone(tag: String) {
        stateSign = 0

        proximityResult = "enter"+tag
        //Log.d("onEnterZone", proximityResult)
        if (binder!=null){
            binder!!.setData(proximityResult)
        }

        // send zone info to server
        //val broadCastingIntent = Intent(this,BroadcastReceiverTest::class.java).apply {
           // putExtra("zone",tag)
        //}
        //val broadCastingIntent = Intent("proximity result to server socket")
        //broadCastingIntent.action = "com.example.PROXIMITY_RESULT"
        //broadCastingIntent.putExtra("zone",tag)
        //sendBroadcast(broadCastingIntent)
        //Log.d("onEnterZone", "$broadCastingIntent")
    }

    override fun onExitZone(tag: String) {

        stateSign = 1
        proximityResult = "exit"+tag
        //Log.d("onExitZone", proximityResult)
        if (binder!=null){
            binder!!.setData(proximityResult)
        }
    }


    // read stateSign every 1s and using this for sliding window
    fun realState(): String? {
        Timer().schedule(1000) {
            stateSign?.let {
                stateArray.add(it)
            }
        }
        Log.d("TEST", "$stateArray")
        val window = stateArray.windowed(size = 5, step = 1)
        val windowAve = window.map { it.average() }
        val lastAve: Double? = windowAve.lastOrNull()
        var result: String? = null
        lastAve?.let {
            when (it) {
                0.4 -> {
                    val checkState = windowAve.elementAt(windowAve.size - 2)
                    if (checkState > 0.4) {
                        result = "Enter ${BeaconUtils.zone} zone"
                    }
                }
                0.6 -> {
                    val checkState = windowAve.elementAt(windowAve.size - 2)
                    if (checkState < 0.6) {
                        result = "Exit ${BeaconUtils.zone} zone"

                    }
                }

            }
        }
        return result
    }



}





