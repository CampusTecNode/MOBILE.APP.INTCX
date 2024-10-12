package com.intec.connect.ui.allproducts

import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.Product
import com.intec.connect.databinding.FragmentAllProductsListBinding
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.interfaces.LikeClickListener
import com.intec.connect.ui.activities.BottomNavigationActivity
import com.intec.connect.ui.adapters.AllProductAdapter
import com.intec.connect.ui.detailsProducts.ProductDetailActivity
import com.intec.connect.ui.home.HomeViewModel
import com.intec.connect.utilities.Constants
import com.intec.connect.utilities.Constants.TOKEN_KEY
import com.intec.connect.utilities.Constants.USERID_KEY
import com.intec.connect.utilities.animations.ReboundAnimator
import com.intec.connect.utilities.animations.ReboundAnimator.ReboundAnimatorType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllProductsFragment : Fragment(), BottomNavigationActivity.KeyboardVisibilityListener {
    private var _binding: FragmentAllProductsListBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val likedProducts = mutableSetOf<Int>()
    private lateinit var allProductAdapter: AllProductAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllProductsListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPrefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString(USERID_KEY, "")!!
        token = sharedPrefs.getString(TOKEN_KEY, "")!!
        (requireActivity() as? BottomNavigationActivity)?.addKeyboardVisibilityListener(this)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupAllProductsRecyclerView()
        animateViewEntrance()
        setupSearchEditText()

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupSearchEditText() {
        binding.searchIdEditTextAllProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().trim().lowercase()

                allProductAdapter.filter(searchText)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    /**
     * Sets up observers for the ViewModel's LiveData objects such as loading state,
     * categories, and products.
     */
    private fun setupObservers() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        userId.let {
            token.let { it1 ->
                homeViewModel.getProducts(it, it1).observe(viewLifecycleOwner) { result ->
                    handleProductsResult(result)
                }
            }
        }
    }

    private fun handleProductsResult(result: Result<List<Product>>) {
        result.onSuccess { products ->
            allProductAdapter.updateProducts(products)
        }.onFailure { e ->
            Log.e("HomeFragment", "Error fetching categories and products", e)
        }
    }

    private fun setupAllProductsRecyclerView() {
        binding.recyclerViewAllProducts.setHasFixedSize(true)
        binding.recyclerViewAllProducts.layoutManager = GridLayoutManager(context, 2)
        allProductAdapter = AllProductAdapter(object : ClickListener<Product> {
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
        binding.recyclerViewAllProducts.adapter = allProductAdapter

        allProductAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                checkEmptyState()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                checkEmptyState()
            }
        })
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

    private fun checkEmptyState() {
        if (allProductAdapter.itemCount == 0) {
            binding.emptyStage.mainContainer.visibility = View.VISIBLE
            binding.recyclerViewAllProducts.visibility = View.GONE
            binding.emptyStage.productNameText.text =
                getString(R.string.el_producto_que_busca_no_se_encuentra_en_inventario)
            binding.emptyStage.productImage.setImageResource(R.drawable.inventory_2_24dp)

        } else {
            binding.emptyStage.mainContainer.visibility = View.GONE
            binding.recyclerViewAllProducts.visibility = View.VISIBLE
        }
    }

    /**
     * Displays or hides the loading animation based on the loading state.
     *
     * @param isLoading Boolean indicating whether to show or hide the loading animation.
     */
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerFrameLayoutLikeView.visibility = View.VISIBLE
            binding.shimmerFrameLayoutLikeView.startShimmer()
            // Hide the content views while loading
            binding.mainContainer.visibility = View.GONE // Add this line
        } else {
            binding.shimmerFrameLayoutLikeView.stopShimmer()
            binding.shimmerFrameLayoutLikeView.visibility = View.GONE
            // Show the content views after loading
            binding.mainContainer.visibility = View.VISIBLE // Add this line
        }
    }

    /**
     * Animates the entrance of various views when the fragment is displayed.
     */
    private fun animateViewEntrance() {
        val viewsToAnimate = listOf(
            binding.title,
            binding.searchIdEditTextAllProduct,
            binding.backArrow
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

    override fun onKeyboardVisibilityChanged(isVisible: Boolean) {
        val params = binding.mainContainer.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = if (isVisible) {
            0  // 0dp cuando el teclado es visible
        } else {
            resources.getDimensionPixelSize(R.dimen.scroll_view_margin_bottom) // 85dp cuando está oculto
        }
        binding.mainContainer.layoutParams = params
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (requireActivity() as? BottomNavigationActivity)?.removeKeyboardVisibilityListener(this)

    }
}