package com.intec.connect.ui.holders

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R

class HomeViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    var productImage: ImageView = itemView.findViewById(R.id.product_image)
    var favoriteButton: ImageButton = itemView.findViewById(R.id.favorite_button)
    var productName: TextView = itemView.findViewById(R.id.product_name_text)
    var productPrice: TextView = itemView.findViewById(R.id.product_price_text)

}