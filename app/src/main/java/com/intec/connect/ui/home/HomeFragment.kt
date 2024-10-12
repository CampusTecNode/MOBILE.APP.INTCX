package com.intec.connect.ui.home

import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.CategoriesProductsItem
import com.intec.connect.data.model.Product
import com.intec.connect.databinding.FragmentHomeBinding
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.interfaces.LikeClickListener
import com.intec.connect.ui.activities.BottomNavigationActivity
import com.intec.connect.ui.adapters.CategoriesAdapter
import com.intec.connect.ui.adapters.CategoriesProductAdapter
import com.intec.connect.ui.adapters.ProductAdapter
import com.intec.connect.ui.detailsProducts.ProductDetailActivity
import com.intec.connect.ui.shopping.CartActivity
import com.intec.connect.utilities.Constants
import com.intec.connect.utilities.Constants.TOKEN_KEY
import com.intec.connect.utilities.Constants.USERID_KEY
import com.intec.connect.utilities.ShoppingCartBadgeManager
import com.intec.connect.utilities.animations.ReboundAnimator
import com.intec.connect.utilities.animations.ReboundAnimator.ReboundAnimatorType
import com.intec.connect.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * HomeFragment is responsible for displaying the home screen of the application.
 * It implements the `BottomNavigationActivity.KeyboardVisibilityListener` to handle
 * changes in keyboard visibility and sets up various views such as category lists and products.
 */
@AndroidEntryPoint
class HomeFragment : Fragment(), BottomNavigationActivity.KeyboardVisibilityListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var categoriesProductAdapter: CategoriesProductAdapter
    private lateinit var productAdapter: ProductAdapter
    private val likedProducts = mutableSetOf<Int>()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var token: String
    private lateinit var keyboardVisibilityListener: BottomNavigationActivity.KeyboardVisibilityListener

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var isShimmerShown = false // Flag to track if shimmer has been shown

    /**
     * Inflates the fragment's view and returns the root view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that this fragment's UI will be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment's layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        sharedPrefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString(USERID_KEY, "")!!
        token = sharedPrefs.getString(TOKEN_KEY, "")!!
        keyboardVisibilityListener = this
        (requireActivity() as? BottomNavigationActivity)?.addKeyboardVisibilityListener(
            keyboardVisibilityListener
        )

        return binding.root
    }

    /**
     * Called immediately after the fragment's view has been created.
     * Sets up observers, RecyclerViews, and handles view animations.
     *
     * @param view The view returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupCategoriesRecyclerView()
        setupCategoriesProductsRecyclerView()
        setupAllProductsRecyclerView()
        setupSearchEditText()
        animateViewEntrance()
        setupAddFabClickListener()
    }

    private fun setupAddFabClickListener() {
        binding.bagContainer.setOnClickListener {
            val intent = Intent(requireContext(), CartActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                requireContext(),
                R.anim.activity_transition_from_right,
                R.anim.activity_transition_stay_visible
            )
            startActivity(intent, options.toBundle())
        }
    }
    /**
     * Sets up observers for the ViewModel's LiveData objects such as loading state,
     * categories, and products.
     */
    private fun setupObservers() {

        sharedViewModel.categoriesProducts.observe(viewLifecycleOwner) { result ->
            result.onSuccess { categoriesProducts ->
                categoriesAdapter.updateCategories(categoriesProducts)
                categoriesAdapter.notifyDataSetChanged()
                Log.d("HomeFragment", "categoriesProducts observer triggered")
            }
            result.onFailure { error ->
                Log.e("HomeFragment", "Error refreshing categoriesProducts", error)
            }
        }

        // Observe products LiveData
        sharedViewModel.products.observe(viewLifecycleOwner) { result ->
            result.onSuccess { products ->
                productAdapter.updateProducts(products)
                productAdapter.notifyDataSetChanged()
                Log.d("HomeFragment", "products observer triggered")
            }
            result.onFailure { error ->
                Log.e("HomeFragment", "Error refreshing products", error)
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        userId.let {
            token.let {
                homeViewModel.getCategoriesProducts(userId, token)
                    .observe(viewLifecycleOwner) { result ->
                        handleCategoriesProductsResult(result)
                    }

                homeViewModel.getProducts(userId, token).observe(viewLifecycleOwner) { result ->
                    handleProductsResult(result)
                }
            }
        }
    }

    private fun updateBadgeCount() {
        val badgeCount = ShoppingCartBadgeManager.getInstance().getBadgeCount()
        binding.bagBadge.text = badgeCount.toString()
        binding.bagBadge.visibility = if (badgeCount > 0) View.VISIBLE else View.GONE
    }

    /**
     * Displays or hides the loading animation based on the loading state.
     *
     * @param isLoading Boolean indicating whether to show or hide the loading animation.
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading && !isShimmerShown) {
            binding.shimmerFrameLayoutHomeView.visibility = View.VISIBLE
            binding.shimmerFrameLayoutHomeView.startShimmer()
            binding.mainContainer.visibility = View.GONE
            isShimmerShown = true
        } else if (!isLoading) {
            binding.shimmerFrameLayoutHomeView.stopShimmer()
            binding.shimmerFrameLayoutHomeView.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
        }
    }

    /**
     * Handles the result of fetching categories and products.
     * Updates the adapter with the fetched data or logs an error in case of failure.
     *
     * @param result Result containing a list of categories and products or an error.
     */
    private fun handleCategoriesProductsResult(result: Result<List<CategoriesProductsItem>>) {
        result.onSuccess { categoriesProducts ->
            categoriesAdapter.updateCategories(categoriesProducts)
            categoriesProducts.firstOrNull()?.let {
                categoriesProductAdapter.updateCategoriesProducts(it.products)
            }
        }.onFailure { e ->
            Log.e("HomeFragment", "Error fetching categories and products", e)
        }
    }

    /**
     * Handles the result of fetching products.
     * Updates the product adapter or logs an error in case of failure.
     *
     * @param result Result containing a list of products or an error.
     */
    private fun handleProductsResult(result: Result<List<Product>>) {
        result.onSuccess { products ->
            productAdapter.updateProducts(products)
        }.onFailure { e ->
            Log.e("HomeFragment", "Error fetching products", e)
        }
    }

    /**
     * Sets up the RecyclerView for categories and assigns the adapter.
     */
    private fun setupCategoriesRecyclerView() {
        binding.recyclerViewCategories.setHasFixedSize(true)
        binding.recyclerViewCategories.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        categoriesAdapter = CategoriesAdapter(object : ClickListener<CategoriesProductsItem> {
            override fun onClick(view: View, item: CategoriesProductsItem, position: Int) {
                Toast.makeText(context, item.name, Toast.LENGTH_SHORT).show()
            }
        }, requireActivity(), binding.recyclerViewCategories)
        binding.recyclerViewCategories.adapter = categoriesAdapter

        binding.recyclerViewCategories.post {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.recyclerViewCategories.getChildAt(0)?.performClick()
            }, 100)
        }

        categoriesAdapter.getSelectedCategoryIndex().observe(viewLifecycleOwner) { selectedIndex ->
            if (selectedIndex in 0 until categoriesAdapter.itemCount) {
                val selectedCategory = categoriesAdapter.categoriesProducts()[selectedIndex]
                categoriesProductAdapter.updateCategoriesProducts(selectedCategory.products)
                showEmptyStateIfNoProducts(selectedCategory.products.isEmpty())
            }
        }
    }

    /**
     * Sets up the RecyclerView for category-specific products and assigns the adapter.
     */
    private fun setupCategoriesProductsRecyclerView() {
        binding.recyclerViewProducts.setHasFixedSize(true)
        binding.recyclerViewProducts.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        categoriesProductAdapter = CategoriesProductAdapter(object : ClickListener<Product> {
            override fun onClick(view: View, item: Product, position: Int) {
                startProductDetailActivity(context, item)
            }
        }, object : LikeClickListener {
            override fun onLike(product: Product, position: Int) {

                homeViewModel.likeProduct(userId, product.id.toString(), token)
            }

            override fun onUnlike(product: Product, position: Int) {

                homeViewModel.unlikeProduct(userId, product.id.toString(), token)
            }

        }, requireActivity(), binding.recyclerViewProducts)
        binding.recyclerViewProducts.adapter = categoriesProductAdapter

    }

    fun startProductDetailActivity(context: Context?, item: Product) {
        val intent = Intent(context, ProductDetailActivity::class.java)
        intent.putExtra(Constants.PRODUCT_ID, item.id)

        val options = context?.let {
            ActivityOptionsCompat.makeCustomAnimation(
                it,
                R.anim.activity_transition_from_bottom,
                R.anim.activity_transition_stay_visible
            )
        }
        context?.startActivity(intent, options?.toBundle())
    }

    /**
     * Sets up the RecyclerView for all products and assigns the adapter.
     */
    private fun setupAllProductsRecyclerView() {
        binding.recyclerViewAllProducts.setHasFixedSize(true)
        binding.recyclerViewAllProducts.layoutManager =
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        productAdapter = ProductAdapter(object : ClickListener<Product> {
            override fun onClick(view: View, item: Product, position: Int) {
                startProductDetailActivity(context, item)
            }
        }, object : LikeClickListener {
            override fun onLike(product: Product, position: Int) {

                likedProducts.add(product.id)
                homeViewModel.likeProduct(userId, product.id.toString(), token)

            }

            override fun onUnlike(product: Product, position: Int) {

                homeViewModel.unlikeProduct(userId, product.id.toString(), token)
                likedProducts.remove(product.id)
            }

        }, requireActivity(), binding.recyclerViewAllProducts)
        binding.recyclerViewAllProducts.adapter = productAdapter

        binding.textViewNewProductsVewMore.setOnClickListener {
            findNavController().navigate(R.id.allProductsFragment)
        }
    }

    /**
     * Sets up the search EditText for filtering products.
     */
    private fun setupSearchEditText() {
        binding.searchIdEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim().lowercase()

                categoriesProductAdapter.filter(searchText)
                showEmptyStateForSearch(searchText)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /**
     * Displays an empty state if no products are found.
     *
     * @param isEmpty Boolean indicating whether there are no products to display.
     */
    private fun showEmptyStateIfNoProducts(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStage.mainContainer.visibility = View.VISIBLE
            binding.recyclerViewProducts.visibility = View.GONE
            binding.emptyStage.productNameText.text =
                getString(R.string.esta_categoria_no_tiene_productos_por_el_momento)
        } else {
            binding.emptyStage.mainContainer.visibility = View.GONE
            binding.recyclerViewProducts.visibility = View.VISIBLE
        }
    }

    /**
     * Displays an empty state for search results if no products match the search criteria.
     *
     * @param searchText The text used for filtering the products.
     */
    private fun showEmptyStateForSearch(searchText: String) {
        if (categoriesProductAdapter.itemCount == 0) {
            binding.emptyStage.mainContainer.visibility = View.VISIBLE
            binding.recyclerViewProducts.visibility = View.GONE

            if (searchText.isEmpty()) {
                binding.emptyStage.productNameText.text =
                    getString(R.string.esta_categoria_no_tiene_productos_por_el_momento)
            } else {
                val fullText =
                    getString(R.string.no_se_encontraron_productos_para_la_b_squeda, searchText)
                val spannableString = SpannableString(fullText)
                val startIndex = fullText.indexOf(searchText)
                val endIndex = startIndex + searchText.length

                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding.emptyStage.productNameText.text = spannableString
            }
        } else {
            binding.emptyStage.mainContainer.visibility = View.GONE
            binding.recyclerViewProducts.visibility = View.VISIBLE
        }
    }

    /**
     * Animates the entrance of various views when the fragment is displayed.
     */
    private fun animateViewEntrance() {
        val viewsToAnimate = listOf(
            binding.searchIdEditText,
            binding.textViewTitleCategories,
            binding.textViewNewProducts,
            binding.bagContainer,
            binding.textViewNewProductsVewMore
        )

        val reboundAnimator = ReboundAnimator(
            requireActivity(),
            reboundDirection = ReboundAnimatorType.RIGHT_TO_LEFT
        )

        val animatorDuration = 500
        val startDelayBetweenViews = 100

        reboundAnimator.getReboundAnimatorForViews(
            animatorDuration,
            startDelayBetweenViews,
            *viewsToAnimate.toTypedArray()
        ).let {
            AnimatorSet().apply {
                playTogether(*it)
                start()
            }
        }
    }

    /**
     * Called when the visibility of the keyboard changes.
     *
     * @param isVisible Boolean indicating whether the keyboard is visible or not.
     */
    override fun onKeyboardVisibilityChanged(isVisible: Boolean) {
        _binding?.let { binding -> // Add null check
            val params = binding.scrollView.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = if (isVisible) {
                ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                resources.getDimensionPixelSize(R.dimen.scroll_view_margin_bottom)
            }
            binding.scrollView.layoutParams = params
        }
    }

    /**
     * Cleans up the view binding when the fragment is destroyed.
     */
    override fun onDestroyView() {
        (requireActivity() as? BottomNavigationActivity)?.removeKeyboardVisibilityListener(
            keyboardVisibilityListener
        )
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.refreshProducts(userId, token)
        loadShoppingCart()
    }

    private fun loadShoppingCart() {
        homeViewModel.shoppingCartByUser(userId, token).observe(viewLifecycleOwner) { result ->
            result.onSuccess { shoppingCart ->
                val initialCount = shoppingCart.cartDetails.size
                ShoppingCartBadgeManager.getInstance(initialCount)
                updateBadgeCount()
            }
        }
    }
}