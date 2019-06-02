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
                bluetoothScanner = EstimoteBluetoothScannerFactory(applicationContext).getSimpleScanner()
                Log.d("beacon","${bluetoothScanner}")


                val sticker = bluetoothScanner
                    .estimoteLocationScan()
                    .withBalancedPowerMode()
                    .withOnPacketFoundAction {
                        Log.d("sticker","${it.deviceId}")

                    }
                this.startService(Intent(this,Proximity::class.java))

            },
            onRequirementsMissing = {},
            onError = {}
        )

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
