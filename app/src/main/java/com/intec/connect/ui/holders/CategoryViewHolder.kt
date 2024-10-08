package com.intec.connect.ui.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.CategoriesProductsItem
import com.intec.connect.interfaces.ClickListener

/**
 * ViewHolder class for the categories in the RecyclerView.
 * This class binds category data to the views and handles click events for each item.
 *
 * @param itemView The view representing a single item in the RecyclerView.
 * @param listener The click listener interface to handle click events.
 */
class CategoryViewHolder(
    itemView: View,
    listener: ClickListener<CategoriesProductsItem>
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var item: CategoriesProductsItem? = null

    val categoryImage: ImageView = itemView.findViewById(R.id.image_category)
    val categoryTitle: TextView = itemView.findViewById(R.id.title_category)

    private val clickListener = listener

    init {
        itemView.setOnClickListener(this)
    }

    /**
     * Handles the click event for the item view.
     * Calls the click listener's onClick method when the item is clicked.
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
