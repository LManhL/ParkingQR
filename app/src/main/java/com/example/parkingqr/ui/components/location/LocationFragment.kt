package com.example.parkingqr.ui.components.location

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentLocationBinding
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.utils.BitmapUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
class LocationFragment : BaseFragment(), OnMapReadyCallback, OnMarkerClickListener {

    companion object {
        const val FINE_PERMISSION_CODE = 123
    }

    private lateinit var binding: FragmentLocationBinding
    private lateinit var myMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationViewModel: LocationViewModel by hiltNavGraphViewModels(R.id.locationFragment)
    private val parkingLocationIcon: BitmapDescriptor by lazy {
        BitmapUtil.vectorToBitmap(requireActivity(), R.drawable.parking_location)
    }
    private lateinit var parkingLotList: MutableList<ParkingLot>
    private lateinit var parkingLotSearchAdapter: ParkingLotSearchAdapter

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.stateUi.collect {
                    if (it.isLoading) showLoading() else hideLoading()
                    if (it.error.isNotEmpty()) {
                        showError(it.error)
                        locationViewModel.showError()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.stateUi.map { it.parkingLotList }.distinctUntilChanged()
                    .collect { list ->
                        parkingLotList.clear()
                        parkingLotList.addAll(list)
                        parkingLotSearchAdapter.notifyDataSetChanged()
                        getLastLocation()
                    }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentLocationBinding.inflate(layoutInflater)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        parkingLotList = mutableListOf()
        parkingLotSearchAdapter = ParkingLotSearchAdapter(
            requireActivity(),
            R.layout.item_search_parking_lot_location,
            parkingLotList
        )
        binding.autvSearchLocation.setAdapter(parkingLotSearchAdapter)
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        showBottomNavigation()
        requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
        binding.autvSearchLocation.setOnItemClickListener { _, _, position, id ->
            val myLocation = LatLng(
                parkingLotList[position].location.latitude,
                parkingLotList[position].location.longitude
            )
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.0F))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireActivity()))
        myMap.setOnMarkerClickListener(this)
        val myLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
        myMap.addMarker(MarkerOptions().position(myLocation).title("My location"))
        parkingLotList.forEach { place ->
            val marker = myMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.location)
                    .icon(parkingLocationIcon)
            )
            marker?.tag = place
        }
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.0F))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                showMessage("Quyền truy cập vị trí bị từ chối, xin vui lòng cho phép quyền truy cập vị trí")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    }

    private fun getLastLocation() {
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
                FINE_PERMISSION_CODE
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            currentLocation = location
            val mapFragment: SupportMapFragment =
                childFragmentManager.findFragmentById(R.id.fctvGoogleMapLocation) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val place = marker.tag as? ParkingLot ?: return false
        locationViewModel.getParkingLotRateList(place.id)
        locationViewModel.getParkingLotDetail(place.id)
        val modalBottomSheet = BottomSheetPlaceDetailFragment()
        modalBottomSheet.show(childFragmentManager, BottomSheetPlaceDetailFragment.TAG)
        return false
    }
}