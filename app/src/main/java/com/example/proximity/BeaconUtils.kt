package com.example.proximity

import android.util.Log
import com.estimote.proximity_sdk.api.ProximityZone
import com.estimote.proximity_sdk.api.ProximityZoneBuilder
import java.util.*

object BeaconUtils {
    interface BeaconListener{
        fun onEnterZone(tag: String)
        fun onExitZone(tag: String)

    }
    private val defaultRange = 1.0
    val beaconZones = ArrayList<ProximityZone>()
    var listener: BeaconListener? = null
    private val zone = "table"

    private var lastBeaconDate: Date? = null

    init {
        beaconZones.add(
            ProximityZoneBuilder()
                .forTag(zone)
                .inCustomRange(2.0)
                .onEnter{
                    Log.d("Beacons","Enter")
                    /*if(lastBeaconDate == null || Date().time - lastBeaconDate!!.time < 5000){
                        lastBeaconDate = Date()
                        listener?.onEnterZone(zone)
                    }*/
                    listener?.onEnterZone(zone)
                }
                .onExit {
                    Log.d("Beacons","Exit")
                    listener?.onExitZone(zone)
                }
                .build())

    }
}