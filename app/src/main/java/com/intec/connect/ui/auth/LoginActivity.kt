package com.intec.connect.ui.auth

import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.intec.connect.R
import com.intec.connect.constants.Constants
import com.intec.connect.data.model.LoginModel
import com.intec.connect.databinding.ActivityLoginBinding
import com.intec.connect.ui.activities.BottomNavigationActivity
import com.intec.connect.utilities.ErrorDialogFragment
import com.intec.connect.utilities.animations.ReboundAnimator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import java.util.Base64

/**
 * Activity for user login.
 *
 * This activity handles user authentication by interacting with the `AuthViewModel`
 * to send login requests and process responses. It also manages UI elements
 * such as a progress dialog and animations.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var progressDialog: AlertDialog
    private lateinit var messageTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var messageIndex = 0
    private lateinit var messages: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.post {
            animateViewEntrance()
            binding.mainContainer.visibility = View.VISIBLE
        }

        initProgressDialog()

        binding.signInButton.setOnClickListener { initiateLogin() }

        messages = arrayOf(
            getString(R.string.accessing),
            getString(R.string.pls_wait),
            getString(R.string.viewing_application)
        )

        observeLoginResult()
    }

    /**
     * Initializes the progress dialog.
     */
    private fun initProgressDialog() {
        val builder = AlertDialog.Builder(this, R.style.RoundedAlertDialog)

        val view = layoutInflater.inflate(R.layout.progress_dialog, null)
        messageTextView = view.findViewById(R.id.progress_dialog_message)
        builder.setView(view)
        builder.setCancelable(false)
        progressDialog = builder.create()
    }

    /**
     * Initiates the login process.
     *
     * Collects user credentials, displays the progress dialog,
     * and calls the `loginUser` method in the `AuthViewModel`.
     */
    private fun initiateLogin() {
        if (isInternetAvailable()) {
            progressDialog.show()

            val matriculation = binding.vatIdEditText.text.toString()
            val hashedPassword = hashPassword(binding.passwordEditText.text.toString())

            lifecycleScope.launch {
                authViewModel.loginUser(
                    LoginModel(
                        username = matriculation,
                        password = hashedPassword
                    )
                )
            }
            handler.postDelayed(updateMessageRunnable, 6000)
        } else {
            showErrorAlertDialog(Exception("No internet connection"))
        }
    }

    /**
     * Observes the login result from the `AuthViewModel` and updates the UI.
     *
     * Handles successful login by saving the token and navigating to the
     * `BottomNavigationActivity`. Displays error messages in case of login failure.
     */
    private fun observeLoginResult() {
        authViewModel.loginResult.observe(this) { result ->
            progressDialog.dismiss()
            handler.removeCallbacks(updateMessageRunnable)

            result.onSuccess { tokenModel ->
                saveTokenAndNavigate(tokenModel.toString())
            }.onFailure { e ->
                Log.e("LoginActivity", "Login failed", e as Exception)
                showErrorAlertDialog(e)
            }
        }
    }

    /**
     * Shows an alert dialog with an error message based on the exception.
     *
     * @param e The exception that occurred during login.
     */
    private fun showErrorAlertDialog(e: Exception) {
        val (message, lottieRawRes) = if (e.message?.contains("401") == true) {
            Pair(getString(R.string.invalid_credentials), R.raw.warning)
        } else if (e.message?.contains("No internet connection") == true) {
            Pair(getString(R.string.no_internet_message), R.raw.wifi)
        } else {
            Pair(getString(R.string.service_unavailable), R.raw.errorx)
        }

        val dialogFragment = ErrorDialogFragment.newInstance(message, lottieRawRes)
        dialogFragment.show(supportFragmentManager, "error_dialog")
    }

    /**
     * Saves the authentication token and navigates to the main activity.
     *
     * @param token The authentication token to be saved.
     */
    private fun saveTokenAndNavigate(token: String) {
        val sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("auth_token", token).apply()

        val intent = Intent(this, BottomNavigationActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.activity_transition_from_right,
            R.anim.activity_transition_stay_visible
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    /**
     * Runnable to update the message in the progress dialog.
     */
    private val updateMessageRunnable = object : Runnable {
        override fun run() {
            messageTextView.text = messages[messageIndex]
            messageIndex = (messageIndex + 1) % messages.size
            handler.postDelayed(this, 2350)
        }
    }

    /**
     * Hashes the given password using Argon2 algorithm.
     *
     * @param password The password to be hashed.
     * @return The hashed password string.
     */
    private fun hashPassword(password: String): String {
        val salt = Constants.SALT.toByteArray()

        val generator = Argon2BytesGenerator()
        generator.init(
            Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withParallelism(1)
                .withMemoryAsKB(65536)
                .withIterations(2)
                .build()
        )

        val hash = ByteArray(32)
        generator.generateBytes(password.toByteArray(), hash)

        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder()
            .encodeToString(hash)
    }

    /**
     * Animates the entrance of views in the activity.
     */
    private fun animateViewEntrance() {
        val viewsToAnimate = listOf(
            binding.logo,
            binding.constraintLayout,
            binding.welcomeTextView,
            binding.vatId,
            binding.password,
            binding.signInButton,
            binding.forgotPasswordTextView,
            binding.orContainer,
            binding.vatIdEditText,
            binding.passwordEditText,
            binding.outlookButton
        )

        val reboundAnimator = ReboundAnimator(
            this,
            reboundDirection = ReboundAnimator.ReboundAnimatorType.RIGHT_TO_LEFT
        )

        val animatorDuration = 500
        val startDelayBetweenViews = 100

        val animatorSet = reboundAnimator.getReboundAnimatorForViews(
            animatorDuration,
            startDelayBetweenViews,
            *viewsToAnimate.toTypedArray()
        )

        AnimatorSet().apply {
            playTogether(*animatorSet)
            start()
        }
    }

    /**
     * Checks if the device has an active internet connection.
     *
     * @return `true` if the device has an internet connection, `false` otherwise.
     */
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    override fun onDestroy() {
        super.onDestroy()
        authViewModel.loginResult.removeObservers(this)
    }
}