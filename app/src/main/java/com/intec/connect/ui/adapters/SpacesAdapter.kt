package com.intec.connect.ui.adapters

import android.animation.Animator
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.SpacesItem
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.ui.holders.SpacesViewHolder
import com.intec.connect.utilities.animations.ListViewAnimatorHelper
import com.intec.connect.utilities.animations.ReboundAnimator

class SpacesAdapter(
    private val clickListener: ClickListener<SpacesItem>,
    private val context: Activity?,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<SpacesViewHolder>() {
    private var animatorViewHelper: ListViewAnimatorHelper? = null
    private var reboundAnimatorManager: ReboundAnimator? = null
    private val spacesItems = mutableListOf<SpacesItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpacesViewHolder {
        val layout =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.spaces_items, parent, false)
        animatorViewHelper = context?.let {
            ListViewAnimatorHelper(
                it, recyclerView.layoutManager as LinearLayoutManager
            )
        }
        reboundAnimatorManager =
            context?.let { ReboundAnimator(it, ReboundAnimator.ReboundAnimatorType.RIGHT_TO_LEFT) }

        return SpacesViewHolder(
            itemView = layout,
            listener = clickListener,
        )

    }

    override fun onBindViewHolder(holder: SpacesViewHolder, position: Int) {
        val spaces = spacesItems[position]
        holder.item = spaces

        holder.spacesName.text = spaces.name
        holder.spacesDescription.text = spaces.description
        holder.capacity.text = spaces.capacity.toString()

        if (spaces.available) {
            holder.spaceStatus.setBackgroundColor(ContextCompat.getColor(context!!, R.color.green))
        } else {
            holder.spaceStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context!!,
                    R.color.primary_color
                )
            )

            val animators: Array<Animator> =
                reboundAnimatorManager!!.getReboundAnimatorForView(holder.itemView.rootView)

            animatorViewHelper!!.animateViewIfNecessary(position, holder.itemView, animators)
        }


    }

    override fun getItemCount(): Int = spacesItems.size

    fun updateSpaces(spacesItems: List<SpacesItem>) {
        this.spacesItems.clear()
        this.spacesItems.addAll(spacesItems)
        notifyDataSetChanged()
    }
}