package com.intec.connect.ui.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.SpacesItem
import com.intec.connect.interfaces.ClickListener

/**
 * ViewHolder class for the products in the categories RecyclerView.
 * This class binds product data to the views and handles click events for each item.
 *
 * @param itemView The view representing a single item in the RecyclerView.
 * @param listener The click listener interface to handle click events on products.
 */
class SpacesViewHolder(
    itemView: View,
    listener: ClickListener<SpacesItem>

) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var item: SpacesItem? = null

    var spacesImage: ImageView = itemView.findViewById(R.id.spacesImage)
    var spaceStatus: View = itemView.findViewById(R.id.status_spaces)
    var spacesName: TextView = itemView.findViewById(R.id.name_spaces)
    var spacesDescription: TextView = itemView.findViewById(R.id.description_spaces)
    var capacity: TextView = itemView.findViewById(R.id.cuantity)

    private val clickListener = listener

    init {
        itemView.setOnClickListener(this)
    }

    /**
     * Handles the click event for the product item view.
     * Calls the click listener's onClick method when the product is clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View?) {
        item?.let { currentItem ->
            v?.let {
                clickListener.onClick(it, currentItem, adapterPosition)
            }
        }
    }
}
