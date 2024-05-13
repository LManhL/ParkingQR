package com.example.parkingqr.ui.components.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentParkingLotDetailBinding
import com.example.parkingqr.databinding.FragmentParkingLotDetailHomeBinding
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.user.ParkingLotManager
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.location.LocationFragment
import com.example.parkingqr.ui.components.location.MarkerInfoWindowAdapter
import com.example.parkingqr.utils.FormatCurrencyUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ParkingLotDetailFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentParkingLotDetailHomeBinding
    private val viewModel: HomeViewModel by hiltNavGraphViewModels(R.id.homeFragment)
    private lateinit var adapter: ImageParkingLotDetailAdapter
    private lateinit var myMap: GoogleMap
    private lateinit var currentLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ImageParkingLotDetailAdapter(mutableListOf())
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isLoading }.distinctUntilChanged().collect {
                    if (it) showLoading()
                    else hideLoading()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.parkingLot }.distinctUntilChanged().collect {
                    it?.let {
                        showParkingLot(it)
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isDeletedParkingLot }.distinctUntilChanged().collect {
                    if(it){
                        getNavController().popBackStack()
                        viewModel.resetIsDeletedPara()
                    }
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentParkingLotDetailHomeBinding.inflate(layoutInflater)
        binding.rlvImageParkingLotDetailHome.adapter = adapter
        binding.rlvImageParkingLotDetailHome.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.parking_lot_list_fragment_name))
        binding.btnCreateParkingLotDetailHome.setOnClickListener {
            viewModel.deleteParkingLotByIdThenUpdateParkingLotManager()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireActivity()))
        currentLocation.let { myLocation ->
            myMap.addMarker(MarkerOptions().position(myLocation).title("Location"))
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.0F))
        }
    }

    private fun showParkingLot(parkingLot: ParkingLot) {
        binding.apply {
            edtNameParkingLotDetailHome.setText(parkingLot.name)
            edtAddressParkingLotDetailHome.setText(parkingLot.address)
            edtAreaParkingLotDetailHome.setText(FormatCurrencyUtil.formatNumberCeil(parkingLot.area))
            edtMotorCapaParkingLotDetailHome.setText(parkingLot.motorCapacity.toInt().toString())
            edtCarCapaCrateParkingLot.setText(parkingLot.carCapacity.toInt().toString())
            edtDescripParkingLotDetailHome.setText(parkingLot.description)
            edtPhoneParkingLotDetailHome.setText(parkingLot.phoneNumber)
            when (parkingLot.status) {
                ParkingLot.ParkingLotStatus.PENDING_STATUS -> {
                    tvStatusParkingLotDetailHome.text = "Đang chờ phê duyệt"
                }
                ParkingLot.ParkingLotStatus.ACCEPTED_STATUS -> {}
                ParkingLot.ParkingLotStatus.DECLINED_STATUS -> {
                    tvStatusParkingLotDetailHome.text = "Từ chối phê duyệt"
                }
            }
        }
        adapter.addData(parkingLot.images.toList())
        getLocation(parkingLot)
    }

    private fun getLocation(parkingLot: ParkingLot) {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LocationFragment.FINE_PERMISSION_CODE
            )
            return
        }
        currentLocation = parkingLot.location
        val mapFragment: SupportMapFragment =
            childFragmentManager.findFragmentById(R.id.fctvGoogleMapParkingLotDetailHome) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}