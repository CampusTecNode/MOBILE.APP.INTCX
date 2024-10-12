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
import com.intec.connect.utilities.animations.ListViewAnimatorHelper
import com.intec.connect.utilities.animations.ReboundAnimator

class AllProductAdapter(
    private val clickListener: ClickListener<Product>,
    private val likeClickListener: LikeClickListener,
    private val context: Activity?,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<CategoryProductViewHolder>() {
    private var animatorViewHelper: ListViewAnimatorHelper? = null
    private var reboundAnimatorManager: ReboundAnimator? = null

    private val products = mutableListOf<Product>()
    private var originalProductList: List<Product> = listOf()
    private var filteredProductList: List<Product> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewHolder {
        val layout =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.like_product_adapter_item, parent, false)
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

        if (product.imageURL.isNotEmpty()) {
            context?.let {
                Glide.with(it)
                    .load(product.imageURL)
                    .into(holder.productImage)
            }
        } else {
            holder.productImage.setImageResource(R.drawable.mascotas)
        }

        val animators: Array<Animator> =
            reboundAnimatorManager!!.getReboundAnimatorForView(holder.itemView.rootView)

        animatorViewHelper!!.animateViewIfNecessary(position, holder.itemView, animators)
    }

    override fun getItemCount(): Int = filteredProductList.size

    fun updateProducts(products: List<Product>) {
        this.products.clear()
        this.products.addAll(products)
        this.originalProductList = products
        filter("")
        notifyDataSetChanged()
    }

    fun products(): MutableList<Product> {
        return this.products
    }

    fun filter(text: String) {
        filteredProductList = if (text.isEmpty()) {
            originalProductList
        } else {
            originalProductList.filter { product ->
                product.name.lowercase().contains(text.lowercase())
            }
        }

        products().clear()
        products().addAll(filteredProductList)
        notifyDataSetChanged()
    }
}
