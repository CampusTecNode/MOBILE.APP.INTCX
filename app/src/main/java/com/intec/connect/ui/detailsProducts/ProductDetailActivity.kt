package com.intec.connect.ui.detailsProducts

import android.animation.AnimatorSet
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.intec.connect.R
import com.intec.connect.databinding.ActivityProductsDetailsBinding
import com.intec.connect.utilities.Constants
import com.intec.connect.utilities.Constants.TOKEN_KEY
import com.intec.connect.utilities.Constants.USERID_KEY
import com.intec.connect.utilities.DialogFragmentCart
import com.intec.connect.utilities.ShoppingCartBadgeManager
import com.intec.connect.utilities.animations.ReboundAnimator
import com.intec.connect.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsDetailsBinding
    private val productsDetailViewModel: ProductsDetailViewModel by viewModels()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var token: String
    private var productId: Int = 0
    private var originalPrice = 0.0
    private var isLiked = false
    private var count = 1

    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsDetailsBinding.inflate(layoutInflater)

        productId = intent.getIntExtra(Constants.PRODUCT_ID, -1)

        sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = sharedPrefs.getString(TOKEN_KEY, "")!!
        userId = sharedPrefs.getString(USERID_KEY, "")!!

        setContentView(binding.root)
        updateBadgeCount()

        setupObservers()
        setupCounter()

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                animateViewEntrance()
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishWithAnimation()
            }
        })

        binding.backArrow.setOnClickListener {
            finishWithAnimation()
        }

        binding.favoriteButton.setOnClickListener {
            updateFavoriteButtonAppearance(!isLiked)

            if (isLiked) {
                productsDetailViewModel.unlikeProduct(userId, productId, token)
                    .observe(this) { result ->
                        result.onSuccess {
                            sharedViewModel.refreshHomeData(userId, token)
                        }
                    }
            } else {
                productsDetailViewModel.likeProduct(userId, productId, token)
                    .observe(this) { result ->
                        result.onSuccess {
                            sharedViewModel.refreshHomeData(userId, token)
                        }
                    }
            }
        }

        binding.constraintLayout6.setOnClickListener {
            productsDetailViewModel.shoppingCart(userId, productId, count, token)
                .observe(this) { result ->
                    result.onSuccess {
                        productsDetailViewModel.shoppingCartByUser(userId, token)
                            .observe(this) { result ->
                                result.onSuccess {

                                }
                            }
                    }
                }
        }

        binding.constraintLayout6.setOnClickListener {
            productsDetailViewModel.shoppingCart(userId, productId, count, token)
                .observe(this) { result ->
                    result.onSuccess {
                        productsDetailViewModel.shoppingCartByUser(userId, token)
                            .observe(this) { result ->
                                result.onSuccess { shoppingCart ->
                                    showErrorAlertDialog()

                                    val newCartSize = shoppingCart.cartDetails.size

                                    val badgeManager = ShoppingCartBadgeManager.getInstance()
                                    badgeManager.setBadgeCount(newCartSize)

                                    updateBadgeCount()
                                    resetProductDetails()
                                }
                            }
                    }
                }
        }
    }

    private fun showErrorAlertDialog() {
        val dialogFragment = DialogFragmentCart.newInstance(
            "Producto agregado.", R.raw.add_cart
        )
        dialogFragment.show(supportFragmentManager, "error_dialog")
    }

    private fun updateBadgeCount() {
        val badgeCount = ShoppingCartBadgeManager.getInstance().getBadgeCount()
        binding.bagBadge.text = badgeCount.toString()
        binding.bagBadge.visibility = if (badgeCount > 0) View.VISIBLE else View.GONE
    }

    private fun setupCounter() {
        binding.textViewCount.text = count.toString()

        binding.buttonMinus.setOnClickListener {
            if (count > 1) {
                count--
                binding.textViewCount.text = count.toString()
                updateTotalPrice(count)
            }
        }

        binding.buttonPlus.setOnClickListener {
            count++
            binding.textViewCount.text = count.toString()
            updateTotalPrice(count)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.activity_transition_stay_visible,
            R.anim.activity_transition_to_bottom
        )
    }

    private fun finishWithAnimation() {
        ActivityCompat.finishAfterTransition(this)
    }

    private fun setupObservers() {
        productsDetailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        userId.let {
            token.let {
                productsDetailViewModel.getProductsDetail(productId, userId, token)
                    .observe(this) { result ->
                        result.onSuccess { product ->
                            binding.nameProduct.text = product.name
                            binding.brand.text = product.brand
                            binding.productDetail.text = product.description
                            binding.stockText.text = product.stock.toString()
                            binding.colorText.text = product.color
                            binding.weightText.text = product.weight.toString()
                            binding.sizeText.text = product.size
                            binding.skuText.text = product.sku
                            binding.productPriceText.text = product.price

                            isLiked = product.liked
                            updateFavoriteButtonAppearance(isLiked)

                            binding.productPriceText.text = product.price
                            originalPrice = product.price.toDoubleOrNull() ?: 0.0
                            updateTotalPrice(1)

                        }.onFailure { error ->
                            Log.e(
                                "ProductDetailActivity",
                                "Error al obtener los detalles del producto",
                                error
                            )
                        }
                    }
            }
        }
    }

    private fun resetProductDetails() {
        count = 1
        binding.textViewCount.text = count.toString()
        updateTotalPrice(count)
    }

    private fun updateTotalPrice(count: Int) {
        val totalPrice = originalPrice * count
        binding.productPriceText.text = totalPrice.toString()
    }

    private fun updateFavoriteButtonAppearance(isLiked: Boolean) {

        if (isLiked) {
            binding.favoriteButton.setImageResource(R.drawable.favorite_24dp_fill_red)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.favorite_24dp_fill)
        }
    }

    /**
     * Displays or hides the loading animation based on the loading state.
     *
     * @param isLoading Boolean indicating whether to show or hide the loading animation.
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.mainContainer.visibility = View.GONE
            binding.detailsProductShimmer.visibility = View.VISIBLE
            binding.shimmerContainer.visibility = View.VISIBLE
            binding.detailsProductShimmer.startShimmer()
        } else {
            binding.detailsProductShimmer.stopShimmer()
            binding.detailsProductShimmer.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
            binding.shimmerContainer.visibility = View.GONE
        }
    }

    /**
     * Animates the entrance of views in the activity.
     */
    private fun animateViewEntrance() {
        val viewsToAnimate = listOf(
            binding.title,
            binding.backArrow,
            binding.image,
            binding.bagContainer,
            binding.constraintLayout,
            binding.nameProduct,
            binding.brand,
            binding.favoriteButton,
            binding.productDescription,
            binding.productDetail,
            binding.characteristics,
            binding.characteristicsContainer,
            binding.productPriceContainer,
            binding.constraintLayout6,
            binding.linearLayout2
        )

        val reboundAnimator = ReboundAnimator(
            this,
            reboundDirection = ReboundAnimator.ReboundAnimatorType.BOTTOM_TO_TOP
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

    override fun onDestroy() {
        super.onDestroy()
        productsDetailViewModel.getProductsDetail(-1, "", "").removeObservers(this)
    }
}