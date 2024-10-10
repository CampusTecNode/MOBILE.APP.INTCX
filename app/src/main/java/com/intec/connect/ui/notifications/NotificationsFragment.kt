package com.intec.connect.ui.notifications

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.intec.connect.data.model.NotificationItem
import com.intec.connect.databinding.FragmentNotificationsBinding
import com.intec.connect.ui.adapters.NotificationAdapter

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

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
        fetchNotificationsFromStorage()
    }

    private fun fetchNotificationsFromStorage() {
        val sharedPrefs =
            requireContext().getSharedPreferences("notifications", Context.MODE_PRIVATE)
        val notificationItems = mutableListOf<NotificationItem>()

        for (key in sharedPrefs.all.keys) {
            if (key.startsWith("notification_")) {
                val jsonString = sharedPrefs.getString(key, null)
                if (jsonString != null) {
                    val notificationData = Gson().fromJson(jsonString, NotificationData::class.java)
                    notificationItems.add(
                        NotificationItem(
                            title = notificationData.title ?: "",
                            body = notificationData.body ?: "",
                            timestamp = notificationData.timestamp ?: 0L
                        )
                    )
                }
            }
        }

        // Update the adapter
        notificationAdapter.updateNotifications(notificationItems)
    }

    private data class NotificationData(
        val title: String? = null,
        val body: String? = null,
        val timestamp: Long? = null
    )

    private fun setupNotificationAdapter() {
        binding.notificationRecyclerView.setHasFixedSize(true)
        binding.notificationRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
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
}