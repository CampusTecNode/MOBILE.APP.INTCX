package com.intec.connect.ui.adapters

import android.animation.Animator
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intec.connect.R
import com.intec.connect.data.model.CategoriesProductsItem
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.ui.holders.CategoryViewHolder
import com.intec.connect.utilities.animations.ListViewAnimatorHelper
import com.intec.connect.utilities.animations.ReboundAnimator

/**
 * Adapter for displaying a list of categories in a horizontal RecyclerView.
 *
 * This adapter uses [CategoryViewHolder] to hold individual category items and
 * provides animations for item entrance using [ReboundAnimator] and [ListViewAnimatorHelper].
 *
 * @param clickListener Listener for handling clicks on category items.
 * @param context The activity context.
 * @param recyclerView The RecyclerView where this adapter is attached.
 */
class CategoriesAdapter(
    private val clickListener: ClickListener<CategoriesProductsItem>,
    private val context: Activity?,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<CategoryViewHolder>() {
    private var animatorViewHelper: ListViewAnimatorHelper? = null
    private var reboundAnimatorManager: ReboundAnimator? = null
    private var selectedCategoryIndex = 0
    private var isFirstTime = true
    private val animatedPositions = mutableSetOf<Int>()
    private val _selectedCategoryIndex = MutableLiveData(0)
    fun getSelectedCategoryIndex(): LiveData<Int> = _selectedCategoryIndex

    private val categoriesProducts = mutableListOf<CategoriesProductsItem>()

    /**
     * Called when RecyclerView needs a new [CategoryViewHolder] of the given type to represent
     * an item.
     *
     * This method creates a new ViewHolder and initializes the animation helpers.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view
    type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.category_adapter_item,
                parent, false
            )
        animatorViewHelper = context?.let {
            ListViewAnimatorHelper(
                it,
                recyclerView.layoutManager as LinearLayoutManager
            )
        }
        reboundAnimatorManager =
            context?.let { ReboundAnimator(it, ReboundAnimator.ReboundAnimatorType.RIGHT_TO_LEFT) }

        return CategoryViewHolder(itemView = layout, listener = clickListener)

    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * This method binds the category data to the ViewHolder, sets up click listeners,
     * and applies animations.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(
        holder:
        CategoryViewHolder, position: Int
    ) {
        val categoriesProductsItem = categoriesProducts[position]

        holder.categoryTitle.text = categoriesProductsItem.name

        holder.item = categoriesProductsItem

        holder.categoryTitle.text = categoriesProductsItem.name
        categoriesProductsItem.imageURL

        context?.let {
            Glide.with(it)
                .load(categoriesProductsItem.imageURL)
                .into(holder.categoryImage)
        }

        if (position == selectedCategoryIndex) {
            holder.itemView.isSelected = true
            holder.categoryTitle.visibility = View.VISIBLE
        } else {
            holder.itemView.isSelected = false
            holder.categoryTitle.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            selectedCategoryIndex = if (selectedCategoryIndex == position) {
                position
            } else {
                position
            }
            _selectedCategoryIndex.value = selectedCategoryIndex
            notifyDataSetChanged()
        }

        if (position !in animatedPositions) {
            animateItem(holder, position)
            animatedPositions.add(position)
        }
    }

    /**
     * Animates the given item using the rebound animation.
     *
     * @param holder The ViewHolder of the item to animate.
     * @param position The position of the item in the adapter.
     */
    private fun animateItem(holder: CategoryViewHolder, position: Int) {
        val animators: Array<Animator> =
            reboundAnimatorManager!!.getReboundAnimatorForView(holder.itemView.rootView)

        animatorViewHelper!!.animateViewIfNecessary(position, holder.itemView, animators)

    }

    /**
     * Called when this adapter is attached to a RecyclerView.
     *
     * This method triggers initial animation for all items when the adapter is
     * attached to the RecyclerView for the first time.
     *
     * @param recyclerView The RecyclerView this adapter is attached to.
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        if (isFirstTime) {
            isFirstTime = false
            recyclerView.post {
                (0 until itemCount).forEach { position ->
                    val holder =
                        recyclerView.findViewHolderForAdapterPosition(position) as? CategoryViewHolder
                    holder?.let { animateItem(it, position) }
                }
            }
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this
    adapter.
     */
    override fun getItemCount(): Int = categoriesProducts.size

    /**
     * Updates the category data in the adapter and notifies the RecyclerView
     * that the data has changed.
     *
     * @param categoriesProducts The new list of categories to display.
     */
    fun updateCategories(categoriesProducts: List<CategoriesProductsItem>) {
        this.categoriesProducts.clear()
        this.categoriesProducts.addAll(categoriesProducts)
        notifyDataSetChanged()
    }

    /**
     * Returns the current list of categories held by the adapter.
     *
     * @return The list of categories.
     */
    fun categoriesProducts(): MutableList<CategoriesProductsItem> {
        return this.categoriesProducts
    }
}
