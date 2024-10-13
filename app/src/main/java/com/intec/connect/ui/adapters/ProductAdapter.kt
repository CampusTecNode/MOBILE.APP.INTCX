package com.intec.connect.ui.adapters

import android.animation.Animator
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intec.connect.R
import com.intec.connect.data.model.Product
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.interfaces.LikeClickListener
import com.intec.connect.ui.holders.CategoryProductViewHolder
import com.intec.connect.utilities.DoubleClickHelper
import com.intec.connect.utilities.animations.ListViewAnimatorHelper
import com.intec.connect.utilities.animations.ReboundAnimator

class ProductAdapter(
    private val clickListener: ClickListener<Product>,
    private val likeClickListener: LikeClickListener,
    private val context: Activity?,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<CategoryProductViewHolder>() {
    private var animatorViewHelper: ListViewAnimatorHelper? = null
    private var reboundAnimatorManager: ReboundAnimator? = null
    private val doubleClickHelper =
        DoubleClickHelper<Product, CategoryProductViewHolder>(likeClickListener)

    private val products = mutableListOf<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.home_adapter_item, parent, false)
        animatorViewHelper = context?.let {
            ListViewAnimatorHelper(
                it,
                recyclerView.layoutManager as LinearLayoutManager
            )
        }
        reboundAnimatorManager =
            context?.let { ReboundAnimator(it, ReboundAnimator.ReboundAnimatorType.RIGHT_TO_LEFT) }

        return CategoryProductViewHolder(
            itemView = layout,
            listener = clickListener,
            likeClickListener = likeClickListener
        )

    }

    override fun onBindViewHolder(holder: CategoryProductViewHolder, position: Int) {
        val product = products[position]
        holder.item = product

        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productPrice.text = product.price

        holder.updateFavoriteButtonAppearance(product.liked)

        context?.let {
            Glide.with(it)
                .load(product.imageURL)
                .into(holder.productImage)
        }


        holder.itemView.setOnClickListener { view ->
            doubleClickHelper.handleDoubleClick(
                view,
                holder,
                product,
                position,
                clickListener
            )
        }

        val animators: Array<Animator> =
            reboundAnimatorManager!!.getReboundAnimatorForView(holder.itemView.rootView)

        animatorViewHelper!!.animateViewIfNecessary(position, holder.itemView, animators)
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(categoriesProducts: List<Product>) {
        this.products.clear()
        this.products.addAll(categoriesProducts)
        notifyDataSetChanged()
    }

    fun products(): MutableList<Product> {
        return this.products
    }

}
