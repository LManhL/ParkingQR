package com.example.parkingqr.ui.components.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentBottomSheetPlaceDetailBinding
import com.example.parkingqr.databinding.FragmentBottomSheetRecentDistanceListBinding
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.parkinglot.ParkingLotWithDistance
import com.example.parkingqr.domain.model.parkinglot.Rate
import com.example.parkingqr.utils.FormatCurrencyUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BottomSheetRecentDistanceListFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "BottomSheetRecentDistanceListFragment"
    }

    private lateinit var binding: FragmentBottomSheetRecentDistanceListBinding
    private lateinit var recentDisParkingLotAdapter: RecentDisParkingLotAdapter
    private val locationViewModel: LocationViewModel by hiltNavGraphViewModels(R.id.locationFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recentDisParkingLotAdapter = RecentDisParkingLotAdapter(mutableListOf()) {
            onClickItem(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetRecentDistanceListBinding.inflate(layoutInflater)
        binding.rlvBottomSheetRecentDistanceList.adapter = recentDisParkingLotAdapter
        binding.rlvBottomSheetRecentDistanceList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        observerViewModel()
        return binding.root
    }

    private fun observerViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.uiState.map { it.nearbyParkingLots }.distinctUntilChanged()
                    .collect {
                        recentDisParkingLotAdapter.addAll(it)
                    }
            }
        }
    }

    private fun onClickItem(parkingLotWithDistance: ParkingLotWithDistance) {
        locationViewModel.getParkingLotInfo(parkingLotWithDistance.parkingLot.id)
    }
}