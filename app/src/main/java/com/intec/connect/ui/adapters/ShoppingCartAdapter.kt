package com.intec.connect.ui.adapters

import android.animation.Animator
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.CartDetail
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.ui.holders.ShoppingViewHolder
import com.intec.connect.utilities.animations.ListViewAnimatorHelper
import com.intec.connect.utilities.animations.ReboundAnimator

class ShoppingCartAdapter(
    private val clickListener: ClickListener<CartDetail>,
    private val context: Activity?,
    private val recyclerView: RecyclerView,
    private val onDeleteClickListener: (List<Int>) -> Unit
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

    override fun onBindViewHolder(holder: ShoppingViewHolder, position: Int) {
        val shoppingCart = shoppingCartItems[position]
        holder.item = shoppingCart

        val itemId = shoppingCartItems[position].product.id

        holder.productName.text = shoppingCart.product.name
        holder.productDescription.text = shoppingCart.product.brand
        holder.productPrice.text = shoppingCart.product.price
        holder.productQuantity.text = shoppingCart.quantity.toString()

        holder.productCheckbox.isChecked = selectedItems.contains(itemId)

        holder.productCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(itemId)
            } else {
                selectedItems.remove(itemId)
            }
        }

        holder.productImage.setImageResource(R.drawable.mascotas)

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