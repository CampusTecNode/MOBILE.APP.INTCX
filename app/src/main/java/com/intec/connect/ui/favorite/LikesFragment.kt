package com.intec.connect.ui.favorite

import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.Product
import com.intec.connect.databinding.FragmentLikesBinding
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.interfaces.LikeClickListener
import com.intec.connect.ui.adapters.LikesAdapter
import com.intec.connect.ui.detailsProducts.ProductDetailActivity
import com.intec.connect.utilities.Constants
import com.intec.connect.utilities.Constants.TOKEN_KEY
import com.intec.connect.utilities.Constants.USERID_KEY
import com.intec.connect.utilities.animations.ReboundAnimator
import com.intec.connect.utilities.animations.ReboundAnimator.ReboundAnimatorType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikesFragment : Fragment() {
    private var _binding: FragmentLikesBinding? = null
    private val binding get() = _binding!!
    private val likesViewModel: LikesViewModel by viewModels()

    private lateinit var likesAdapter: LikesAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLikesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedPrefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPrefs.getString(USERID_KEY, "")!!
        token = sharedPrefs.getString(TOKEN_KEY, "")!!

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupProductsRecyclerView()
        animateViewEntrance()
    }

    /**
     * Sets up observers for the ViewModel's LiveData objects such as loading state,
     * categories, and products.
     */
    private fun setupObservers() {
        likesViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        userId.let {
            token.let { it1 ->
                likesViewModel.getLikedProducts(it, it1).observe(viewLifecycleOwner) { result ->
                    handleProductsResult(result)
                }
            }
        }
    }

    private fun handleProductsResult(result: Result<List<Product>>) {
        result.onSuccess { products ->
            likesAdapter.updateLikedProducts(products)
        }.onFailure { e ->
            Log.e("HomeFragment", "Error fetching categories and products", e)
        }
    }

    private fun setupProductsRecyclerView() {
        binding.recyclerViewLikes.setHasFixedSize(true)
        binding.recyclerViewLikes.layoutManager = GridLayoutManager(context, 2)
        likesAdapter = LikesAdapter(object : ClickListener<Product> {
            override fun onClick(view: View, item: Product, position: Int) {
                startProductDetailActivity(context, item)
            }


        }, object : LikeClickListener {
            override fun onLike(product: Product, position: Int) {
            }

            override fun onUnlike(product: Product, position: Int) {
                likesViewModel.unlikeProduct(userId, product.id.toString(), token)
                likesAdapter.removeItem(position)
            }

        }, requireActivity(), binding.recyclerViewLikes)
        binding.recyclerViewLikes.adapter = likesAdapter

        likesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
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
        if (likesAdapter.itemCount == 0) {
            binding.emptyStage.mainContainer.visibility = View.VISIBLE
            binding.recyclerViewLikes.visibility = View.GONE
            binding.emptyStage.productNameText.text =
                getString(R.string.no_tienes_productos_como_favoritos)
        } else {
            binding.emptyStage.mainContainer.visibility = View.GONE
            binding.recyclerViewLikes.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        userId.let {
            token.let { it1 ->
                likesViewModel.getLikedProducts(it, it1).observe(viewLifecycleOwner) { result ->
                    handleProductsResult(result)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Animates the entrance of various views when the fragment is displayed.
     */
    private fun animateViewEntrance() {
        val viewsToAnimate = listOf(
            binding.title
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
}