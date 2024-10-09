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

// ... imports

/**
 * Adapter for displaying a list of products in a horizontal RecyclerView.
 *
 * This adapter uses [CategoryProductViewHolder] to hold individual product items and
 * provides animations for item entrance using [ReboundAnimator] and [ListViewAnimatorHelper].
 *
 * @param clickListener Listener for handling clicks on product items.
 * @param context The activity context.
 * @param recyclerView The RecyclerView where this adapter is attached.
 */
class CategoriesProductAdapter(
    private val clickListener: ClickListener<Product>,
    private val likeClickListener: LikeClickListener,
    private val context: Activity?,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<CategoryProductViewHolder>() {
    private var animatorViewHelper: ListViewAnimatorHelper? = null
    private var reboundAnimatorManager: ReboundAnimator? = null
    private val categoriesProducts = mutableListOf<Product>()
    private var originalProductList: List<Product> = listOf()
    private var filteredProductList: List<Product> = listOf()

    /**
     * Called when RecyclerView needs a new [CategoryProductViewHolder] of the given type to represent
     * an item.
     *
     * This method creates a new ViewHolder and initializes the animation helpers.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
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

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * This method binds the product data to the ViewHolder and applies animations.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: CategoryProductViewHolder, position: Int) {
        val product = categoriesProducts[position]
        holder.item = product

        holder.productName.text = product.name
        holder.productDescription.text = product.description
        holder.productPrice.text = product.price.split(".")[0]
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

    /**
     * Returns the total number of items in the filtered product list.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = filteredProductList.size

    /**
     * Filters the product list based on the provided text.
     *
     * @param text The text to filter the products by.
     */
    fun filter(text: String) {
        filteredProductList = if (text.isEmpty()) {
            originalProductList
        } else {
            originalProductList.filter { product ->
                product.name.lowercase().contains(text.lowercase())
            }
        }

        categoriesProducts.clear()
        categoriesProducts.addAll(filteredProductList)
        notifyDataSetChanged()
    }

    /**
     * Updates the product list in the adapter and notifies the RecyclerView
     * that the data has changed.
     *
     * @param categoriesProducts The new list of products to display.
     */
    fun updateCategoriesProducts(categoriesProducts: List<Product>) {
        this.categoriesProducts.clear()
        this.categoriesProducts.addAll(categoriesProducts)
        this.originalProductList = categoriesProducts
        this.filteredProductList = categoriesProducts
        filter("")
        notifyDataSetChanged()
    }

    /**
     * Returns the current list of products held by the adapter.
     *
     * @return The list of products.
     */
    fun categoriesProducts(): MutableList<Product> {
        return this.categoriesProducts
    }
}