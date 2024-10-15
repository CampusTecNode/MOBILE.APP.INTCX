package com.intec.connect.ui.shopping

import android.animation.AnimatorSet
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.api.stripe.Ultils.PUBLISHABLE_KEY
import com.intec.connect.data.model.CartDetail
import com.intec.connect.databinding.ActivityBagBinding
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.interfaces.DeleteModeListener
import com.intec.connect.ui.adapters.ShoppingCartAdapter
import com.intec.connect.utilities.Constants.TOKEN_KEY
import com.intec.connect.utilities.Constants.USERID_KEY
import com.intec.connect.utilities.DialogFragmentCart
import com.intec.connect.utilities.ShoppingCartBadgeManager
import com.intec.connect.utilities.animations.ReboundAnimator
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartActivity : AppCompatActivity(), DeleteModeListener {
    private lateinit var binding: ActivityBagBinding
    private val shoppingViewModel: ShoppingViewModel by viewModels()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var token: String
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter
    private var isDeleteMode = false
    private var isShimmerShown = false

    private val badgeManager = ShoppingCartBadgeManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentConfiguration.init(this, PUBLISHABLE_KEY)

        sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = sharedPrefs.getString(TOKEN_KEY, "")!!
        userId = sharedPrefs.getString(USERID_KEY, "")!!

        binding = ActivityBagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.post {
            animateViewEntrance()
        }

        binding.backArrow.setOnClickListener {
            finish()
        }
        binding.goToPay.setOnClickListener {
        }

        binding.deleteItem.setOnClickListener {
            isDeleteMode = !isDeleteMode
            shoppingCartAdapter.showCheckBoxes(isDeleteMode)
            shoppingCartAdapter.updateDeleteMode(isDeleteMode)
        }


        setupObservers()
        setupShoppingCartRecyclerView()
    }


    private fun showErrorAlertDialog() {
        val dialogFragment = DialogFragmentCart.newInstance(
            "Pago Exitoso", R.raw.add_cart
        )
        dialogFragment.show(supportFragmentManager, "error_dialog")
    }

    private fun setupObservers() {
        shoppingViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        shoppingViewModel.shoppingCartByUser(userId, token).observe(this) { result ->
            result.onSuccess { shoppingCart ->
                shoppingCartAdapter.updateShoppingCart(listOf(shoppingCart))

                if (shoppingCart.cartDetails.isNotEmpty()) {
                    val totalPrice = calculateTotalPrice(shoppingCart.cartDetails)
                    binding.productPriceText.text = totalPrice.toString()
                }
            }
            result.onFailure { error ->
                // Handle the error, e.g., show an error message to the user
                Log.e("CartActivity", "Error loading shopping cart", error)
            }
        }
    }

    /**
     * Displays an empty state if no products are found.
     *
     */
    private fun showEmptyStateIfNoProducts() {
        if (shoppingCartAdapter.itemCount == 0) {
            binding.emptyStage.visibility = View.VISIBLE
            binding.shoppingCartRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStage.visibility = View.GONE
            binding.shoppingCartRecyclerView.visibility = View.VISIBLE
        }

    }

    private fun calculateTotalPrice(cartDetails: List<CartDetail>): Double {
        var totalPrice = 0.0
        for (item in cartDetails) {
            val price = item.product.price.toDoubleOrNull() ?: 0.0
            val quantity = item.quantity
            totalPrice += price * quantity
        }
        return totalPrice
    }

    private fun setupShoppingCartRecyclerView() {
        binding.shoppingCartRecyclerView.setHasFixedSize(true)
        binding.shoppingCartRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        shoppingCartAdapter = ShoppingCartAdapter(object : ClickListener<CartDetail> {
            override fun onClick(view: View, item: CartDetail, position: Int) {

            }
        }, this, binding.shoppingCartRecyclerView, onDeleteClickListener = { cartId, cartDetail ->
            shoppingViewModel.deleteShoppingCartItem(
                cartId,
                cartDetail.product.id,
                token
            )

            badgeManager.decrementBadgeCount()

        }, isDeleteMode, this)

        binding.shoppingCartRecyclerView.adapter = shoppingCartAdapter

        shoppingCartAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                showEmptyStateIfNoProducts()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                showEmptyStateIfNoProducts()
            }
        })
    }

    /**
     * Displays or hides the loading animation based on the loading state.
     *
     * @param isLoading Boolean indicating whether to show or hide the loading animation.
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading && !isShimmerShown) {
            binding.shoppingCartShimmer.visibility = View.VISIBLE
            binding.shoppingCartShimmer.startShimmer()
            binding.mainContainer.visibility = View.GONE
            isShimmerShown = true
        } else if (!isLoading) {
            binding.shoppingCartShimmer.stopShimmer()
            binding.shoppingCartShimmer.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
        }
    }

    private fun animateViewEntrance() {
        val viewsToAnimate = listOf(
            binding.backArrow,
            binding.deleteItem,
            binding.amountBalance,
            binding.productPriceText,
            binding.symbolMoney,
            binding.productImage,
            binding.productNameText,
            binding.productPriceContainer,
            binding.goToPay,
            binding.title
        )

        val reboundAnimator = ReboundAnimator(
            this,
            reboundDirection = ReboundAnimator.ReboundAnimatorType.RIGHT_TO_LEFT
        )

        val animatorDuration = 500
        val startDelayBetweenViews = 100

        val animatorSet = reboundAnimator.getReboundAnimatorForViews(
            animatorDuration,
            startDelayBetweenViews,
            *viewsToAnimate.toTypedArray()
        )

        AnimatorSet().apply {
            playTogether(*animatorSet)
            start()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.activity_transition_stay_visible,
            R.anim.activity_transition_to_right
        )
    }

    override fun isDeleteMode(): Boolean {
        return isDeleteMode
    }

    override fun onDestroy() {
        super.onDestroy()
        shoppingViewModel.shoppingCartByUser(userId, token).removeObservers(this)
    }

    private fun updateBadgeCount(count: Int) {
        // No es necesario hacer nada aquí
    }
}