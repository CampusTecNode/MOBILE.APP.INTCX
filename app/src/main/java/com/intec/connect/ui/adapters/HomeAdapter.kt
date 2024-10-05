package com.intec.connect.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.CategoriesProductsItem
import com.intec.connect.ui.holders.HomeViewHolder
import com.intec.connect.utilities.animations.ListViewAnimatorHelper
import com.intec.connect.utilities.animations.ReboundAnimator

class HomeAdapter(
    private val context: Activity?,
    private val recyclerView: RecyclerView,
) : RecyclerView.Adapter<HomeViewHolder>() {
    private var animatorViewHelper: ListViewAnimatorHelper? = null
    private var reboundAnimatorManager: ReboundAnimator? = null
    private val categoriesProducts = mutableListOf<CategoriesProductsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(
            R.layout.home_adapter_item, parent, false
        )
        animatorViewHelper = context?.let {
            ListViewAnimatorHelper(
                it, recyclerView.layoutManager as LinearLayoutManager
            )
        }
        reboundAnimatorManager =
            context?.let { ReboundAnimator(it, ReboundAnimator.ReboundAnimatorType.RIGHT_TO_LEFT) }

        return HomeViewHolder(
            itemView = layout
        )

    }

    fun updatePokemonList(regionsResult: List<CategoriesProductsItem>) {
        this.categoriesProducts.clear()
        this.categoriesProducts.addAll(regionsResult)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val categoriesProductsItem = categoriesProducts[position]

        for (product in categoriesProductsItem.products) {
            holder.productName.text = product.name
            holder.productPrice.text = product.price
        }
        holder.productImage.setImageResource(R.drawable.mascotas)

    }

    override fun getItemCount(): Int = categoriesProducts.size
}
