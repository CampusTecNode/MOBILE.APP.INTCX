package com.intec.connect.ui.adapters

import android.animation.Animator
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.Notification
import com.intec.connect.utilities.animations.ListViewAnimatorHelper
import com.intec.connect.utilities.animations.ReboundAnimator
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter(
    private val recyclerView: RecyclerView,
    private val context: Activity?
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private var animatorViewHelper: ListViewAnimatorHelper? = null
    private var reboundAnimatorManager: ReboundAnimator? = null

    private val notifications = mutableListOf<Notification>()

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.productTitle)
        val bodyTextView: TextView = itemView.findViewById(R.id.productPrice)
        val timeTextView: TextView = itemView.findViewById(R.id.timeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.notification_item,
            parent, false
        )

        animatorViewHelper = context?.let {
            ListViewAnimatorHelper(
                it,
                recyclerView.layoutManager as LinearLayoutManager
            )
        }
        reboundAnimatorManager = context?.let {
            ReboundAnimator(it, ReboundAnimator.ReboundAnimatorType.RIGHT_TO_LEFT)
        }

        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.titleTextView.text = notification.title
        holder.bodyTextView.text = notification.message

        val formattedDate = formatDate(notification.createdAt)
        holder.timeTextView.text = formattedDate

        val animators: Array<Animator> =
            reboundAnimatorManager!!.getReboundAnimatorForView(holder.itemView.rootView)
        animatorViewHelper!!.animateViewIfNecessary(position, holder.itemView, animators)
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

        try {
            val date = inputFormat.parse(dateString)
            return outputFormat.format(date)

        } catch (e: ParseException) {
            Log.e("NotificationAdapter", "Error al formatear la fecha", e)
            return dateString // Devolver la fecha original en caso de error
        }
    }

    override fun getItemCount(): Int = notifications.size

    fun updateNotifications(newNotifications: List<Notification>) {
        this.notifications.clear()
        this.notifications.addAll(newNotifications)
        notifyDataSetChanged()
    }
}