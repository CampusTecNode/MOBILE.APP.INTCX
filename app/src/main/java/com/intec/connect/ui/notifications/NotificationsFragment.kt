package com.intec.connect.ui.notifications

import android.animation.AnimatorSet
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.databinding.FragmentNotificationsBinding
import com.intec.connect.ui.adapters.NotificationAdapter
import com.intec.connect.utilities.Constants.TOKEN_KEY
import com.intec.connect.utilities.Constants.USERID_KEY
import com.intec.connect.utilities.animations.ReboundAnimator
import com.intec.connect.utilities.animations.ReboundAnimator.ReboundAnimatorType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val notificationsViewModel: NotificationsViewModel by viewModels()

    private val binding get() = _binding!!
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState:
        Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNotificationAdapter()
        val sharedPrefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString(USERID_KEY, "") ?: ""
        val token = sharedPrefs.getString(TOKEN_KEY, "") ?: ""

        notificationsViewModel.getNotifications(userId, token)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess { notifications ->
                    notificationAdapter.updateNotifications(notifications)
                }
                result.onFailure { error ->
                    Log.e("NotificationsFragment", "Error al obtener las notificaciones", error)
                }
            }
        animateViewEntrance()
    }

    private fun setupNotificationAdapter() {
        binding.notificationRecyclerView.setHasFixedSize(true)
        binding.notificationRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        notificationAdapter =
            NotificationAdapter(binding.notificationRecyclerView, requireActivity())
        binding.notificationRecyclerView.adapter = notificationAdapter

        binding.notificationRecyclerView.post {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.notificationRecyclerView.getChildAt(0)?.performClick()
            }, 100)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Animates the entrance of various views when the fragment is displayed.
     */
    private fun animateViewEntrance() {
        val viewsToAnimate = listOf(
            binding.title
        )

        val reboundAnimator = ReboundAnimator(
            requireActivity(),
            reboundDirection = ReboundAnimatorType.RIGHT_TO_LEFT
        )

        val animatorDuration = 500
        val startDelayBetweenViews = 100

        reboundAnimator.getReboundAnimatorForViews(
            animatorDuration,
            startDelayBetweenViews,
            *viewsToAnimate.toTypedArray()
        ).let {
            AnimatorSet().apply {
                playTogether(*it)
                start()
            }
        }
    }
}