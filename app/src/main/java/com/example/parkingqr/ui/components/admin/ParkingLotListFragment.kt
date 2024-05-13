package com.example.parkingqr.ui.components.admin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentParkingLotListBinding
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.ui.base.BaseFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ParkingLotListFragment : BaseFragment() {

    private lateinit var binding: FragmentParkingLotListBinding
    private lateinit var adapter: ParkingLotAdapter
    private val viewModel: ParkingLotListViewModel by hiltNavGraphViewModels(R.id.parkingLotListFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ParkingLotAdapter(mutableListOf()) {
            onClickItem(it)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.parkingLotList }.distinctUntilChanged().collect {
                    adapter.addAll(it)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isLoading }.distinctUntilChanged().collect {
                    if (it) showLoading()
                    else hideLoading()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentParkingLotListBinding.inflate(layoutInflater)
        binding.rlvParkingLotList.adapter = adapter
        binding.rlvParkingLotList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.parking_lot_list_fragment_name))
    }

    override fun onDestroy() {
        super.onDestroy()
        hideActionBar()
    }

    private fun onClickItem(parkingLot: ParkingLot) {
        viewModel.selectItem(parkingLot)
        getNavController().navigate(R.id.parkingLotDetailFragment)
    }
}