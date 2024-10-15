package com.intec.connect.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.intec.connect.databinding.FragmentProfileBinding
import com.intec.connect.utilities.Constants.USER_NAME
import com.intec.connect.utilities.Constants.USER_LAST_NAME

class ProfileFragment : Fragment() {

private var _binding: FragmentProfileBinding? = null
  private val binding get() = _binding!!

  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val profileViewModel =
        ViewModelProvider(this)[ProfileViewModel::class.java]

    _binding = FragmentProfileBinding.inflate(inflater, container, false)
    val root: View = binding.root
    val sharedPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userName = sharedPrefs.getString(USER_NAME, "") ?: ""
    val userLastName = sharedPrefs.getString(USER_LAST_NAME, "") ?: ""
    val fullName = "$userName $userLastName"
    val textView: TextView = binding.profileName
    profileViewModel.text.observe(viewLifecycleOwner) {
      textView.text = fullName
    }
    return root
  }


override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}