package com.example.proximity

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import com.estimote.internal_plugins_api.scanning.BluetoothScanner
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.estimote.scanning_plugin.api.EstimoteBluetoothScannerFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var bluetoothScanner: BluetoothScanner
    //private telemetryScanHandler: ScanHandler? = null

    //private lateinit var notification: Notification



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //notification = NotificationCreator().createNotification(this)

        //Requirements check
        RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
            this,
            onRequirementsFulfilled = {
                Log.d("Beacons","onRequirementsFulfilled")

                // start proximity service
                val proximityServiceIntent = Intent(this,ProximityService::class.java)
                ContextCompat.startForegroundService(this,proximityServiceIntent)
                //ProximityService.enqueueWork(this,proximityServiceIntent)


                // start SocketServer Service


            },
            onRequirementsMissing = {},
            onError = {}
        )

    }

    override fun onStart() {
        super.onStart()
        val socketServerServiceIntent = Intent(this,SocketServerService::class.java)
        socketServerServiceIntent.putExtra("test","hahah")
        SocketServerService.enqueueWork(this,socketServerServiceIntent)
    }


    override fun onDestroy() {
        super.onDestroy()

    }
}
