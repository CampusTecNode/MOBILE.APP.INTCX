package com.intec.connect.ui.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.intec.connect.R
import com.intec.connect.databinding.ActivityBottomNavigationBinding
import com.intec.connect.ui.shopping.CartActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding
    private var keyboardVisibilityListener: KeyboardVisibilityListener? = null
    private var keyboardVisibilityListeners =
        mutableListOf<KeyboardVisibilityListener>() // Add this line

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        setupNavigation()
        setupKeyboardVisibilityListener()
        setupAddFabClickListener()
        FirebaseApp.initializeApp(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener

            }

            val token = task.result
            Log.d(TAG, "FCM registration token: $token")
        }
    }

    /**
     * Initializes the view binding for this activity.
     */
    private fun initializeBinding() {
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Sets up the navigation components with the BottomNavigationView.
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_bottom_navigation) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        keyboardVisibilityListener =
            navHostFragment.childFragmentManager.fragments.firstOrNull() as? KeyboardVisibilityListener
    }

    /**
     * Sets up the click listener for the Add FAB.
     */
    private fun setupAddFabClickListener() {
        binding.addFab.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.activity_transition_from_right,
                R.anim.activity_transition_stay_visible
            )
            startActivity(intent, options.toBundle())
        }
    }

    /**
     * Sets up the keyboard visibility listener to manage UI changes.
     */
    private fun setupKeyboardVisibilityListener() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            private var isKeyboardShowing = false

            override fun onGlobalLayout() {
                val r = Rect()
                rootView.getWindowVisibleDisplayFrame(r)
                val screenHeight = rootView.rootView.height
                val keypadHeight = screenHeight - r.bottom

                if (keypadHeight > screenHeight * 0.15) {
                    if (!isKeyboardShowing) {
                        isKeyboardShowing = true
                        toggleUIVisibility(isVisible = false)
                    }
                } else {
                    if (isKeyboardShowing) {
                        isKeyboardShowing = false
                        toggleUIVisibility(isVisible = true)
                    }
                }
            }
        })
    }

    /**
     * Toggles the visibility of the FAB and BottomAppBar.
     *
     * @param isVisible Boolean indicating whether the UI components should be visible.
     */
    private fun toggleUIVisibility(isVisible: Boolean) {
        if (isVisible) {
            binding.addFab.show()
            val params = binding.bottomAppBar.layoutParams
            params.height =
                resources.getDimensionPixelSize(R.dimen.scroll_view_margin_bottom_inverse)
            binding.bottomAppBar.layoutParams = params
        } else {
            binding.addFab.hide()
            val params = binding.bottomAppBar.layoutParams
            params.height = 1
            binding.bottomAppBar.layoutParams = params
        }

        for (listener in keyboardVisibilityListeners) {
            listener.onKeyboardVisibilityChanged(!isVisible)
        }
    }

    fun addKeyboardVisibilityListener(listener: KeyboardVisibilityListener) {
        keyboardVisibilityListeners.add(listener)
    }

    fun removeKeyboardVisibilityListener(listener: KeyboardVisibilityListener) {
        keyboardVisibilityListeners.remove(listener)
    }

    /**
     * Interface for listening to keyboard visibility changes.
     */
    interface KeyboardVisibilityListener {
        fun onKeyboardVisibilityChanged(isVisible: Boolean)
    }
}