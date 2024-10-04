package com.intec.connect.utilities

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieAnimationView
import com.intec.connect.R

class ErrorDialogFragment : DialogFragment() {

    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var messageTextView: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.RoundedAlertDialog)
        val view = layoutInflater.inflate(R.layout.error_dialog, null)

        lottieAnimationView = view.findViewById(R.id.lottieAnimationView)
        messageTextView = view.findViewById(R.id.dialog_message)
        val closeButton: TextView = view.findViewById(R.id.close)

        val message = arguments?.getString(MESSAGE_KEY) ?: ""
        val lottieRawRes = arguments?.getInt(LOTTIE_KEY) ?: R.raw.errorx

        messageTextView.text = message
        lottieAnimationView.setAnimation(lottieRawRes)

        closeButton.setOnClickListener {
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }

    companion object {
        private const val MESSAGE_KEY = "message"
        private const val LOTTIE_KEY = "lottie_raw_res"

        fun newInstance(message: String, lottieRawRes: Int): ErrorDialogFragment {
            val fragment = ErrorDialogFragment()
            val args = Bundle()
            args.putString(MESSAGE_KEY, message)
            args.putInt(LOTTIE_KEY, lottieRawRes)
            fragment.arguments = args
            return fragment
        }
    }
}