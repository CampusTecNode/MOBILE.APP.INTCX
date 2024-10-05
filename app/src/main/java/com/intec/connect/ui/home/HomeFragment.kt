package com.intec.connect.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.intec.connect.databinding.FragmentHomeBinding
import com.intec.connect.ui.adapters.HomeAdapter
import com.intec.connect.utilities.Constants.TOKEN_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private lateinit var homeAdapter: HomeAdapter

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        setup()
    }

    private fun initObserver() {
        val sharedPrefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPrefs.getString(TOKEN_KEY, null)

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            val loader = binding.shimmerFrameLayout.isShimmerStarted == it
            val loaderS = binding.shimmerFrameLayoutSecond.isShimmerStarted == it
            binding.shimmerFrameLayout.showShimmer(loader)
            binding.shimmerFrameLayoutSecond.showShimmer(loaderS)
        }

        if (token != null) {
            homeViewModel.getCategoriesProducts(token).observe(
                viewLifecycleOwner
            ) {
                it.onSuccess { categoriesProducts ->

                    binding.shimmerFrameLayout.isShimmerStarted
                    binding.shimmerFrameLayout.stopShimmer()
                    binding.shimmerFrameLayout.visibility = View.GONE

                    binding.shimmerFrameLayoutSecond.isShimmerStarted
                    binding.shimmerFrameLayoutSecond.stopShimmer()
                    binding.shimmerFrameLayoutSecond.visibility = View.GONE

                    homeAdapter.updatePokemonList(categoriesProducts)

                }.onFailure { e ->
                    e.message
                }
            }

            Log.d("DashboardFragment", "Token recuperado: $token")

        } else {
            requireActivity().finish()
        }

    }

    private fun setup() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        homeAdapter = HomeAdapter(requireActivity(), binding.recyclerView)

        binding.recyclerView.adapter = homeAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}