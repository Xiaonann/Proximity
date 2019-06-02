package com.example.proximity

/**
 *  using service for proximity(bluetooth need to always running)
 *  first without worker thread later can add
 *
 */

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials
import com.estimote.proximity_sdk.api.ProximityObserver
import com.estimote.proximity_sdk.api.ProximityObserverBuilder
import java.util.*
import kotlin.concurrent.schedule


class Proximity: Service(), BeaconUtils.BeaconListener {

    private var mObservationHandler: ProximityObserver.Handler? = null
    // enter 0 exit 1 for sliding window (or should from outside)
    private var stateSign: Int? = null
    private var stateArray = ArrayList<Int>()
    val timer = Timer()



    // Cloud credentials found from https://cloud.estimote.com/
    private val cloudCredentials =
        EstimoteCloudCredentials("laboratorium-dibris-gmail--kfg", "90e1b9d8344624e9c2cd42b9f5fd6392")

    override fun onCreate() {
        super.onCreate()
        BeaconUtils.listener = this


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val proximityObserver = ProximityObserverBuilder(applicationContext, cloudCredentials)
            .withBalancedPowerMode()
            .onError { throwable: Throwable -> Log.d("Beacons", throwable.toString()) }
            .build()

        mObservationHandler = proximityObserver.startObserving(BeaconUtils.beaconZones)

        return START_NOT_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
        mObservationHandler?.stop()


    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onEnterZone(tag: String): Int {
        Log.d("onEnterZone", tag)
        stateSign = 0

        // send zone info to server
        val broadCastingIntent = Intent()
        broadCastingIntent.action = "proximity result to server socket"
        broadCastingIntent.putExtra("zone",tag)
        sendBroadcast(broadCastingIntent)

        return 0

    }

    override fun onExitZone(tag: String) {
        Log.d("onExitZone", tag)
        stateSign = 1
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





