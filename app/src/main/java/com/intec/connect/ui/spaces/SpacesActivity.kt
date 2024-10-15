package com.intec.connect.ui.spaces

import android.animation.AnimatorSet
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.R
import com.intec.connect.data.model.SpacesItem
import com.intec.connect.databinding.ActivitySpacesBinding
import com.intec.connect.interfaces.ClickListener
import com.intec.connect.ui.adapters.SpacesAdapter
import com.intec.connect.utilities.Constants.TOKEN_KEY
import com.intec.connect.utilities.Constants.USERID_KEY
import com.intec.connect.utilities.animations.ReboundAnimator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpacesActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpacesBinding
    private val spacesViewModel: SpacesViewModel by viewModels()
    private lateinit var spacesAdapter: SpacesAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var userId: String
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = sharedPrefs.getString(TOKEN_KEY, "")!!
        userId = sharedPrefs.getString(USERID_KEY, "")!!

        binding = ActivitySpacesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.post {
            animateViewEntrance()
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        setupObservers()
        setupProductsRecyclerView()

    }

    /**
     * Displays an empty state if no products are found.
     *
     */
    private fun showEmptyStateIfNoProducts() {
        if (spacesAdapter.itemCount == 0) {
            binding.emptyStage.visibility = View.VISIBLE
            binding.spacesRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStage.visibility = View.GONE
            binding.spacesRecyclerView.visibility = View.VISIBLE
        }

    }

    private fun setupObservers() {
        spacesViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        spacesViewModel.getSpacesList(token).observe(this) { result ->
            when {
                result.isSuccess -> {
                    val spacesList = result.getOrNull()
                    if (spacesList != null) {
                        spacesAdapter.updateSpaces(spacesList)
                    }
                }

                result.isFailure -> {
                }
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.shoppingCartShimmer.visibility = View.VISIBLE
            binding.shoppingCartShimmer.startShimmer()
            binding.mainContainer.visibility = View.GONE
        } else {
            binding.shoppingCartShimmer.stopShimmer()
            binding.shoppingCartShimmer.visibility = View.GONE
            binding.mainContainer.visibility = View.VISIBLE
        }
    }

    private fun setupProductsRecyclerView() {
        binding.spacesRecyclerView.setHasFixedSize(true)
        binding.spacesRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        spacesAdapter = SpacesAdapter(object : ClickListener<SpacesItem> {
            override fun onClick(view: View, item: SpacesItem, position: Int) {

            }

        }, this, binding.spacesRecyclerView)
        binding.spacesRecyclerView.adapter = spacesAdapter

        spacesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                showEmptyStateIfNoProducts()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                showEmptyStateIfNoProducts()
            }
        })
    }


    private fun animateViewEntrance() {
        val viewsToAnimate = listOf(
            binding.backArrow,
            binding.productImage,
            binding.productNameText,
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
}