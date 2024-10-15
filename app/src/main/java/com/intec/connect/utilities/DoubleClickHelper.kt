package com.intec.connect.utilities

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.data.model.Product
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.interfaces.LikeClickListener
import com.intec.connect.ui.holders.CategoryProductViewHolder

class DoubleClickHelper<T, VH : RecyclerView.ViewHolder>(private val likeClickListener: LikeClickListener) {

    private var lastClickTime = 0L
    private var isDoubleClick = false

    fun handleDoubleClick(
        view: View,
        holder: VH,
        item: T,
        position: Int,
        clickListener: ClickListener<T>
    ) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 200) {
            isDoubleClick = true

            if (holder is CategoryProductViewHolder && holder.isLiked) {
                playLottieAnimation(holder).let {
                    return
                }
            } else {
                likeClickListener.onLike(item as Product, position)
                if (holder is CategoryProductViewHolder) {
                    playLottieAnimation(holder)
                }
            }

        } else {
            lastClickTime = currentTime
            isDoubleClick = false
            Handler(Looper.getMainLooper()).postDelayed({
                if (!isDoubleClick) {
                    clickListener.onClick(view, item, position)
                }
            }, 200)
        }
    }

    private fun playLottieAnimation(holder: CategoryProductViewHolder) {
        holder.lottieAnimationView.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            holder.lottieAnimationView.visibility = View.GONE
        }, 800)

        holder.lottieAnimationView.playAnimation()
    }
}