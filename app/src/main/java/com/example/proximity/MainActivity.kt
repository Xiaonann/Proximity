package com.example.proximity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.estimote.internal_plugins_api.scanning.BluetoothScanner
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.estimote.scanning_plugin.api.EstimoteBluetoothScannerFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var bluetoothScanner: BluetoothScanner
    //private telemetryScanHandler: ScanHandler? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Requirements check
        RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
            this,
            onRequirementsFulfilled = {
                Log.d("Beacons","onRequirementsFulfilled")

                // start proximity service
                val proximityServiceIntent = Intent(this,Proximity::class.java)
                Proximity.enqueueWork(this,proximityServiceIntent)

                // start SocketServer Service
                //val socketServerServiceIntent = Intent(this,SocketServerService::class.java)
                //SocketServerService.enqueueWork(this,socketServerServiceIntent)

            },
            onRequirementsMissing = {},
            onError = {}
        )

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}
