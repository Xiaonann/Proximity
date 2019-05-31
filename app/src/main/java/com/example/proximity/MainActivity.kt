package com.example.proximity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Requirements check
        RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
            this,
            onRequirementsFulfilled = {
                Log.d("Beacons","onRequirementsFulfilled")
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
