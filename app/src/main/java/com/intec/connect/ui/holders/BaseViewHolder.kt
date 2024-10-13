package com.intec.connect.ui.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.data.model.Product
import com.intec.connect.interfaces.LikeClickListener

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var isLiked: Boolean = false

    fun handleLikeClick(
        item: Product,
        likeClickListener: LikeClickListener
    ) {
        item.liked = !item.liked
        isLiked = item.liked

        if (item.liked) {
            likeClickListener.onLike(item, adapterPosition)
        } else {
            likeClickListener.onUnlike(item, adapterPosition)
        }
    }
}