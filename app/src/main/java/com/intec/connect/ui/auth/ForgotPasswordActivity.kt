package com.intec.connect.ui.auth

import android.animation.AnimatorSet
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.intec.connect.R
import com.intec.connect.data.model.SendResetPassword
import com.intec.connect.databinding.ActivityForgotPasswordBinding
import com.intec.connect.utilities.DialogFragmentCart
import com.intec.connect.utilities.ErrorDialogFragment
import com.intec.connect.utilities.animations.ReboundAnimator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private var isLoginInProgress = false
    private lateinit var progressDialog: AlertDialog
    private val authViewModel: AuthViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private var messageIndex = 0
    private lateinit var messages: Array<String>
    private lateinit var messageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initProgressDialog()

        binding.resetPasswordButton.setOnClickListener { initiate() }

        binding.emailInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.resetPasswordButton.isEnabled = true

                    binding.emailError.visibility = View.GONE
                    binding.emailValidateError.visibility = View.GONE
                } else {

                    binding.resetPasswordButton.isEnabled = false

                    binding.emailError.visibility = View.VISIBLE
                    binding.emailValidateError.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.root.post {
            animateViewEntrance()
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        observeLoginResult()
    }

    /**
     * Initializes the view binding for this activity.
     */
    private fun initializeBinding() {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initiate() {
        if (!isLoginInProgress) {
            isLoginInProgress = true
            binding.resetPasswordButton.isEnabled = false

            progressDialog.show()

            val email = binding.emailInput.text.toString()

            lifecycleScope.launch {
                authViewModel.sendResetPasswordRequest(
                    SendResetPassword(email = email)
                )
            }

            handler.postDelayed(updateMessageRunnable, 6000)
        } else {
            showErrorAlertDialog(Exception("No internet connection"))
        }
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

    private fun observeLoginResult() {
        authViewModel.resetPasswordResult.observe(this) { result ->
            progressDialog.dismiss()
            handler.removeCallbacks(updateMessageRunnable)
            binding.resetPasswordButton.isEnabled = true
            isLoginInProgress = false

            result.onSuccess { _ ->
                showErrorAlertDialog()
            }.onFailure { e ->
                Log.e("ForgotPasswordActivity", "Email send failed", e as Exception)
                showErrorAlertDialog(e)
            }
        }
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
        messages = arrayOf("Enviando correo...", "Por favor espere...")

    }

    private fun showErrorAlertDialog() {
        val dialogFragment = DialogFragmentCart.newInstance(
            "Contraseña enviada al correo", R.raw.succes
        )
        dialogFragment.show(supportFragmentManager, "error_dialog")
    }

    /**
     * Shows an alert dialog with an error message based on the exception.
     *
     * @param e The exception that occurred during login.
     */
    private fun showErrorAlertDialog(e: Exception) {
        val (message, lottieRawRes) = if (e.message?.contains("404") == true) {
            Pair(getString(R.string.failed_email), R.raw.warning)
        } else {
            Pair(getString(R.string.service_unavailable), R.raw.error)
        }

        val dialogFragment = ErrorDialogFragment.newInstance(message, lottieRawRes)
        dialogFragment.show(supportFragmentManager, "error_dialog")
    }

    /**
     * Animates the entrance of views in the activity.
     */
    private fun animateViewEntrance() {
        val viewsToAnimate = listOf(
            binding.backArrow,
            binding.fingerprintIcon,
            binding.forgotPasswordTitle,
            binding.forgotPasswordDescription,
            binding.emailLabel,
            binding.emailInput,
            binding.resetPasswordButton,
            binding.backToLogin
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


}