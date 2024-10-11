package com.intec.connect.ui.adapters

import android.animation.Animator
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.NotificationItem
import com.intec.connect.utilities.animations.ListViewAnimatorHelper
import com.intec.connect.utilities.animations.ReboundAnimator

class NotificationAdapter(
    private val recyclerView: RecyclerView,
    private val context: Activity?
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private var animatorViewHelper: ListViewAnimatorHelper? = null
    private var reboundAnimatorManager: ReboundAnimator? = null

    private val notifications = mutableListOf<NotificationItem>()

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

        // Initialize animation helpers
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
        holder.bodyTextView.text = notification.body
        holder.timeTextView.text = notification.timestamp.toString()

        val animators: Array<Animator> =
            reboundAnimatorManager!!.getReboundAnimatorForView(holder.itemView.rootView)
        animatorViewHelper!!.animateViewIfNecessary(position, holder.itemView, animators)

    }

    override fun getItemCount(): Int = notifications.size

    fun updateNotifications(newNotifications: List<NotificationItem>) {
        this.notifications.clear()
        this.notifications.addAll(newNotifications)
        notifyDataSetChanged()
    }
}