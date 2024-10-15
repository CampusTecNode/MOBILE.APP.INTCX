package com.intec.connect.utilities

import android.animation.Animator
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieAnimationView
import com.intec.connect.R
import com.intec.connect.ui.shopping.CartActivity

class PayDialog : DialogFragment() {

    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var messageTextView: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.RoundedAlertDialog)
        val view = layoutInflater.inflate(R.layout.dialog_cart, null)

        lottieAnimationView = view.findViewById(R.id.lottieAnimationView)
        messageTextView = view.findViewById(R.id.dialog_message_cart)
        val closeButton: TextView = view.findViewById(R.id.close_cart)
        val goCartButton: TextView = view.findViewById(R.id.go_cart)

        val message = arguments?.getString(MESSAGE_KEY) ?: ""
        val lottieRawRes = arguments?.getInt(LOTTIE_KEY) ?: R.raw.error

        messageTextView.text = message
        lottieAnimationView.setAnimation(lottieRawRes)

        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                Handler(Looper.getMainLooper()).postDelayed({
                    requireActivity().finish()
                }, 600)

            }
        })

        lottieAnimationView.playAnimation()

        closeButton.setOnClickListener {
            requireActivity().finish()
        }

        goCartButton.setOnClickListener {
            setupAddFabClickListener()
        }

        builder.setView(view)
        builder.setCancelable(false)
        return builder.create()
    }

    private fun setupAddFabClickListener() {
        val intent = Intent(requireContext(), CartActivity::class.java)
        val options = ActivityOptionsCompat.makeCustomAnimation(
            requireContext(),
            R.anim.activity_transition_from_right,
            R.anim.activity_transition_stay_visible
        )
        startActivity(intent, options.toBundle())
    }

    companion object {
        private const val MESSAGE_KEY = "message"
        private const val LOTTIE_KEY = "lottie_raw_res"

        fun newInstance(message: String, lottieRawRes: Int): PayDialog {
            val fragment = PayDialog()
            val args = Bundle()
            args.putString(MESSAGE_KEY, message)
            args.putInt(LOTTIE_KEY, lottieRawRes)
            fragment.arguments = args
            return fragment
        }
    }
}