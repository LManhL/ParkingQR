package com.example.parkingqr.ui.components.admin

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
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.user.ParkingLotManager
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.location.LocationFragment
import com.example.parkingqr.ui.components.location.MarkerInfoWindowCustomAdapter
import com.example.parkingqr.utils.FormatCurrencyUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ParkingLotDetailFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentParkingLotDetailBinding
    private val viewModel: ParkingLotListViewModel by hiltNavGraphViewModels(R.id.parkingLotListFragment)
    private lateinit var adapter: ImageParkingLotDetailAdapter
    private lateinit var myMap: GoogleMap
    private lateinit var currentLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ImageParkingLotDetailAdapter(mutableListOf())
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.map { it.selectedParkingLot }.distinctUntilChanged()
                    .collect { parkingLot ->
                        parkingLot?.let {
                            showParkingLot(it)
                        }
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.uiState.map { it.parkingLotManager }.distinctUntilChanged()
                    .collect { parkingLotManager ->
                        parkingLotManager?.let {
                            showParkingLotManagerInfo(it)
                        }
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isUpdated }.distinctUntilChanged().collect {
                    if (it) {
                        showMessage("Cập nhật thành công")
                        viewModel.showUpdate()
                        getNavController().popBackStack()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isLoading }.collect {
                    if (it) showLoading()
                    else hideLoading()
                }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentParkingLotDetailBinding.inflate(layoutInflater)
        binding.rlvImageParkingLotDetail.adapter = adapter
        binding.rlvImageParkingLotDetail.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewModel.getParkingLotManager()
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.parking_lot_list_fragment_name))
        binding.btnAcceptParkingLotDetail.setOnClickListener {
            viewModel.acceptParkingLot()
        }
        binding.btnDeclineParkingLotDetail.setOnClickListener {
            viewModel.declineParkingLot()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.setInfoWindowAdapter(MarkerInfoWindowCustomAdapter(requireActivity()))
        currentLocation.let { myLocation ->
            myMap.addMarker(MarkerOptions().position(myLocation).title("Location"))
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.0F))
        }
    }

    private fun showParkingLot(parkingLot: ParkingLot) {
        binding.apply {
            edtNameParkingLotDetail.setText(parkingLot.name)
            edtAddressParkingLotDetail.setText(parkingLot.address)
            edtAreaParkingLotDetail.setText(FormatCurrencyUtil.formatNumberCeil(parkingLot.area))
            edtMotorCapaParkingLotDetail.setText(parkingLot.motorCapacity.toInt().toString())
            edtCarCapaCrateParkingLot.setText(parkingLot.carCapacity.toInt().toString())
            edtDescripParkingLotDetail.setText(parkingLot.description)
            if (parkingLot.status != ParkingLot.ParkingLotStatus.PENDING_STATUS) {
                btnAcceptParkingLotDetail.visibility = View.GONE
                btnDeclineParkingLotDetail.visibility = View.GONE
            } else {
                btnAcceptParkingLotDetail.visibility = View.VISIBLE
                btnAcceptParkingLotDetail.visibility = View.VISIBLE
            }
        }
        adapter.addData(parkingLot.images.toList())
        getLocation(parkingLot)
    }

    private fun showParkingLotManagerInfo(parkingLotManager: ParkingLotManager) {
        binding.apply {
            edtGmailParkingLotDetail.setText(parkingLotManager.account.email)
            edtPhoneParkingLotDetail.setText(parkingLotManager.account.phoneNumber)
        }
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
            childFragmentManager.findFragmentById(R.id.fctvGoogleMapParkingLotDetail) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}