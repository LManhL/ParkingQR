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
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentBottomSheetPlaceDetailBinding
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.parkinglot.Rate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class BottomSheetPlaceDetailFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "BottomSheetDialogFragment"
        const val PARKING_LOT_ID = "PARKING_LOT_ID"
    }

    private lateinit var binding: FragmentBottomSheetPlaceDetailBinding
    private lateinit var rateListAdapter: RateListAdapter
    private lateinit var rateList: MutableList<Rate>
    private val locationViewModel: LocationViewModel by hiltNavGraphViewModels(R.id.locationFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.stateUi.collect {
                    rateList.clear()
                    rateList.addAll(it.rates)
                    rateListAdapter.notifyDataSetChanged()
                    bindView(it.parkingLotDetail)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetPlaceDetailBinding.inflate(layoutInflater)
        rateList = mutableListOf()
        rateListAdapter = RateListAdapter(rateList)
        rateListAdapter.setEventClick {
            handleClickItem(it)
        }
        binding.rlvRateListBottomSheetPlaceDetail.apply {
            adapter = rateListAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        binding.tvChooseRegisterMonthlyInvoice.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(PARKING_LOT_ID, locationViewModel.stateUi.value.parkingLotDetail.id)
            findNavController().navigate(R.id.registerMonthlyInvoiceFragment)
        }
        return binding.root
    }

    private fun bindView(parkingLot: ParkingLot) {
        binding.tvAreaBottomSheetPlaceDetail.text = "${parkingLot.area} m2"
        binding.tvCapacityBottomSheetPlaceDetail.text = parkingLot.capacity
        binding.tvInformationBottomSheetPlaceDetail.text = parkingLot.description
        binding.tvPhoneNumberBottomSheetPlaceDetail.text = parkingLot.phoneNumber
    }

    private fun handleClickItem(rate: Rate) {

    }

}