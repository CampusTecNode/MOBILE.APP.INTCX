package com.intec.connect.ui.holders

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.Product
import com.intec.connect.interfaces.ClickListener

/**
 * ViewHolder class for the products in the categories RecyclerView.
 * This class binds product data to the views and handles click events for each item.
 *
 * @param itemView The view representing a single item in the RecyclerView.
 * @param listener The click listener interface to handle click events on products.
 */
class CategoryProductViewHolder(
    itemView: View,
    listener: ClickListener<Product>
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var item: Product? = null

    var productImage: ImageView = itemView.findViewById(R.id.product_image)
    var favoriteButton: ImageButton = itemView.findViewById(R.id.favorite_button)
    var productDescription: TextView = itemView.findViewById(R.id.product_description_text)
    var productName: TextView = itemView.findViewById(R.id.product_name_text)
    var productPrice: TextView = itemView.findViewById(R.id.product_price_text)

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
