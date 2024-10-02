package com.intec.connect.view.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.intec.connect.R
import com.intec.connect.databinding.ActivityBagBinding

class BagActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBagBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishWithAnimation()
            }
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_transition_stay_visible, R.anim.activity_transition_to_bottom)
    }

    private fun finishWithAnimation() {
        ActivityCompat.finishAfterTransition(this@BagActivity)
    }
}