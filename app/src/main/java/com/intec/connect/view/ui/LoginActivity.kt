package com.intec.connect.view.ui

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.intec.connect.R
import com.intec.connect.databinding.ActivityLoginBinding
import com.intec.connect.utilities.animations.ReboundAnimator

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.post {
            animateViewEntrance()
        }

        binding.signInButton.setOnClickListener {
            val matricula = binding.vatIdEditText.text.toString()
            val contrasena = binding.passwordEditText.text.toString()

            if (matricula.isBlank()) {
                binding.vatIdEditText.error = "Por favor, ingresa tu matrícula"
                return@setOnClickListener
            }

            if (contrasena.isBlank()) {
                binding.passwordEditText.error = "Por favor, ingresa tu contraseña"
                return@setOnClickListener
            }


            val matriculaValida = "1114226"
            val contrasenaValida = "123456789"

            if (matricula != matriculaValida || contrasena != contrasenaValida) {

                binding.vatIdEditText.error = "Credenciales incorrectas"
                binding.passwordEditText.error

                return@setOnClickListener

            } else {
                val intent = Intent(this, BottomNavigationActivity::class.java)

                val options = ActivityOptionsCompat.makeCustomAnimation(
                    this,
                    R.anim.activity_transition_from_right,
                    R.anim.activity_transition_stay_visible
                )

                startActivity(intent, options.toBundle())
                finish()
            }

        }
    }

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
}

