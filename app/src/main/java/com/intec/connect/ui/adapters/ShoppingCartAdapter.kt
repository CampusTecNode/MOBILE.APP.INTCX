package com.intec.connect.ui.adapters

import android.animation.Animator
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intec.connect.R
import com.intec.connect.data.model.CartDetail
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.interfaces.DeleteModeListener
import com.intec.connect.ui.holders.ShoppingViewHolder
import com.intec.connect.utilities.animations.ListViewAnimatorHelper
import com.intec.connect.utilities.animations.ReboundAnimator

class ShoppingCartAdapter(
    private val clickListener: ClickListener<CartDetail>,
    private val context: Activity?,
    private val recyclerView: RecyclerView,
    private val onDeleteClickListener: (CartDetail) -> Unit,
    private var isDeleteMode: Boolean,
    private val deleteModeListener: DeleteModeListener
) : RecyclerView.Adapter<ShoppingViewHolder>() {
    private var animatorViewHelper: ListViewAnimatorHelper? = null
    private var reboundAnimatorManager: ReboundAnimator? = null
    private val shoppingCartItems = mutableListOf<CartDetail>()

    private val selectedItems = mutableListOf<Int>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.shopping_cart_adapter_item, parent, false)
        animatorViewHelper = context?.let {
            ListViewAnimatorHelper(
                it, recyclerView.layoutManager as LinearLayoutManager
            )
        }

        val holder = ShoppingViewHolder(
            itemView = layout,
            listener = clickListener
        )

        holder.productCheckbox.visibility = View.GONE

        reboundAnimatorManager =
            context?.let { ReboundAnimator(it, ReboundAnimator.ReboundAnimatorType.RIGHT_TO_LEFT) }

        return ShoppingViewHolder(
            itemView = layout,
            listener = clickListener
        )

    }

    fun updateDeleteMode(isDeleteMode: Boolean) {
        this.isDeleteMode = isDeleteMode
    }


    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val shoppingCart = shoppingCartItems[position]
        holder.item = shoppingCart

        val itemId = shoppingCartItems[position].product.id

        holder.productName.text = shoppingCart.product.name
        holder.productDescription.text = shoppingCart.product.brand
        holder.productPrice.text = shoppingCart.product.price
        holder.productQuantity.text = shoppingCart.quantity.toString()

        holder.productCheckbox.setOnClickListener {
            if (deleteModeListener.isDeleteMode()) {
                onDeleteClickListener(shoppingCartItems[position])
            }
        }


        if (shoppingCart.product.imageURL.isNotEmpty()) {
            context?.let {
                Glide.with(it)
                    .load(shoppingCart.product.imageURL)
                    .into(holder.productImage)
            }
        } else {
            holder.productImage.setBackgroundResource(R.drawable.fastfood_24dp)
        }


        val animators: Array<Animator> =
            reboundAnimatorManager!!.getReboundAnimatorForView(holder.itemView.rootView)

        animatorViewHelper!!.animateViewIfNecessary(position, holder.itemView, animators)
    }

    override fun getItemCount(): Int = shoppingCartItems.size

    fun updateShoppingCart(shoppingCart: List<CartDetail>) {
        this.shoppingCartItems.clear()
        this.shoppingCartItems.addAll(shoppingCart)
        notifyDataSetChanged()
    }

    fun showCheckBoxes(show: Boolean) {
        for (i in 0 until itemCount) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? ShoppingViewHolder
            holder?.productCheckbox?.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

}