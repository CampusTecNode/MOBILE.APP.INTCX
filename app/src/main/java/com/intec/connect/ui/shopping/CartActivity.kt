package com.intec.connect.ui.shopping

import android.animation.AnimatorSet
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.api.stripe.ApiInterface
import com.intec.connect.api.stripe.ApiUtilities
import com.intec.connect.api.stripe.Ultils.PUBLISHABLE_KEY
import com.intec.connect.data.model.CartDetail
import com.intec.connect.databinding.ActivityBagBinding
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.interfaces.DeleteModeListener
import com.intec.connect.ui.adapters.ShoppingCartAdapter
import com.intec.connect.utilities.Constants.TOKEN_KEY
import com.intec.connect.utilities.Constants.USERID_KEY
import com.intec.connect.utilities.DialogFragmentCart
import com.intec.connect.utilities.animations.ReboundAnimator
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CartActivity : AppCompatActivity(), DeleteModeListener {
    private lateinit var binding: ActivityBagBinding
    private val shoppingViewModel: ShoppingViewModel by viewModels()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var token: String
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter
    private var isDeleteMode = false
    private var isShimmerShown = false // Flag to track if shimmer has been shown
    lateinit var paymentSheet: PaymentSheet
    lateinit var customerId: String
    lateinit var ephemeralKey: String
    lateinit var clientSecretKey: String
    private var apiInterface: ApiInterface = ApiUtilities.getApiInterface()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
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
            payFlow()
        }

        binding.deleteItem.setOnClickListener {
            isDeleteMode = !isDeleteMode
            shoppingCartAdapter.showCheckBoxes(isDeleteMode)
            shoppingCartAdapter.updateDeleteMode(isDeleteMode)
        }


        setupObservers()
        setupShoppingCartRecyclerView()

    }

    private fun payFlow() {
        paymentSheet.presentWithPaymentIntent(
            clientSecretKey,
            PaymentSheet.Configuration(
                "Payment Method",
                PaymentSheet.CustomerConfiguration(
                    customerId, ephemeralKey
                )
            )
        )
    }


    private fun getCustomerId() {
        lifecycleScope.launch {
            Dispatchers.IO

            val res = apiInterface.getCustomer()
            withContext(Dispatchers.Main) {
                if (res.isSuccessful && res.body() != null) {
                    customerId = res.body()!!.id
                    getEphemeralKey(customerId)
                }
            }
        }
    }

    private fun getEphemeralKey(id: String) {
        lifecycleScope.launch {
            Dispatchers.IO

            val res = apiInterface.getEphemeralKey(customer = id)
            withContext(Dispatchers.Main) {
                if (res.isSuccessful && res.body() != null) {
                    ephemeralKey = res.body()!!.id
                    getPaymentIntent(id, ephemeralKey)
                }
            }
        }
    }

    private fun getPaymentIntent(id: String, ephemeralKey: String) {

        lifecycleScope.launch {
            Dispatchers.IO

            val res = apiInterface.getPaymentIntent(customer = id)
            withContext(Dispatchers.Main) {
                if (res.isSuccessful && res.body() != null) {
                    clientSecretKey = res.body()!!.id
                }
            }
        }
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        if (paymentSheetResult is PaymentSheetResult.Completed) {
            showErrorAlertDialog()
        }
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
                shoppingCartAdapter.updateShoppingCart(shoppingCart.cartDetails) // Assuming one cart per user
                // Calculate and display the total price
                val totalPrice = calculateTotalPrice(shoppingCart.cartDetails)
                binding.productPriceText.text = totalPrice.toString()
            }
            result.onFailure { error ->
                // Handle the error, e.g., show an error message to the user
                Toast.makeText(this, "Error loading shopping cart", Toast.LENGTH_SHORT).show()
                Log.e("CartActivity", "Error loading shopping cart", error)
            }
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
        }, this, binding.shoppingCartRecyclerView, onDeleteClickListener = { cartDetail ->
            shoppingViewModel.deleteShoppingCartItem(
                cartDetail.id,
                cartDetail.product.id,
                userId,
                token
            )
        }, isDeleteMode, this)

        binding.shoppingCartRecyclerView.adapter = shoppingCartAdapter

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
}