package com.intec.connect.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.intec.connect.R

class ResetPassword : AppCompatActivity() {
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Called when the activity is starting.
     *
     * This method sets up the splash screen by enabling edge-to-edge display,
     * setting the content view, adjusting padding for system bars, and scheduling
     * a delayed transition to the login activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  **Note: Otherwise it is null.**
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        }

    }

    /**
     * Enables edge-to-edge display for the activity, making the content extend
     * behind the system bars.
     */

