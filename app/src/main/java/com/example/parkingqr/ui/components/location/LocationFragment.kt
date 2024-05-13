package com.example.parkingqr.ui.components.location

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@RequiresApi(Build.VERSION_CODES.R)
class LocationFragment : BaseFragment(), OnMapReadyCallback, OnMarkerClickListener {

    companion object {
        const val FINE_PERMISSION_CODE = 123
    }

    private lateinit var binding: FragmentLocationBinding
    private lateinit var myMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var suggestionParkingLotAdapter: SuggestionParkingLotAdapter
    private lateinit var parkingLotList: MutableList<ParkingLot>
    private val locationViewModel: LocationViewModel by hiltNavGraphViewModels(R.id.locationFragment)
    private val parkingLocationIcon: BitmapDescriptor by lazy {
        BitmapUtil.vectorToBitmap(requireActivity(), R.drawable.parking_location)
    }
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parkingLotList = mutableListOf()
        suggestionParkingLotAdapter = SuggestionParkingLotAdapter(mutableListOf()) {
            onClickSuggestItem(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                showMessage("Quyền truy cập vị trí bị từ chối, xin vui lòng cho phép quyền truy cập vị trí")
            }
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.uiState.map { it.error }.distinctUntilChanged().collect {
                    if (it.isNotEmpty()) {
                        showError(it)
                        locationViewModel.showError()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.uiState.map { it.parkingLotList }.distinctUntilChanged().collect {
                    parkingLotList.clear()
                    parkingLotList.addAll(it)
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.uiState.map { it.suggestParkingLotList }.distinctUntilChanged()
                    .collect {
                        suggestionParkingLotAdapter.addAll(it)
                    }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.uiState.map { it.isLoading }.distinctUntilChanged()
                    .collect {
                        if (it) showLoading() else hideLoading()
                    }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.uiState.map { it.isShowParkingLotDetail }.distinctUntilChanged()
                    .collect {
                        if (it) {
                            BottomSheetPlaceDetailFragment().show(
                                childFragmentManager,
                                BottomSheetPlaceDetailFragment.TAG
                            )
                            locationViewModel.showParkingLotDetail()
                        }
                    }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentLocationBinding.inflate(layoutInflater)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        mapFragment =
            childFragmentManager.findFragmentById(R.id.fctvGoogleMapLocation) as SupportMapFragment
        binding.rlvSuggestionParkingLotLocation.apply {
            adapter = suggestionParkingLotAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        getLastLocation()
        return binding.root
    }

    override fun initListener() {
        hideActionBar()
        showBottomNavigation()
        requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
        binding.edtSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                handleSearchParkingLot(s.toString())
            }
        })
        binding.edtSearchLocation.setOnClickListener {
            showSearchList(true)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireActivity()))
        myMap.setOnMarkerClickListener(this)
        myMap.setOnMapClickListener {
            showSearchList(false)
        }
        val myLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
        myMap.addMarker(MarkerOptions().position(myLocation).title("My location"))
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15.0F))
        showParkingLotList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val place = marker.tag as? ParkingLot ?: return false
        locationViewModel.getParkingLotInfo(place.id)
        return false
    }

    private fun showSearchList(isShow: Boolean) {
        if (isShow) {
            binding.rlvSuggestionParkingLotLocation.visibility = View.VISIBLE
        } else {
            binding.rlvSuggestionParkingLotLocation.visibility = View.GONE
        }
    }

    private fun handleSearchParkingLot(name: String) {
        if (name.isNotEmpty()) {
            searchParkingLotByName(name)
        } else {
            locationViewModel.clearSuggestList()
        }
    }

    private fun searchParkingLotByName(name: String) {
        searchJob?.cancel()
        searchJob = CoroutineScope(Dispatchers.Main).launch {
            delay(200)
            locationViewModel.searchParkingLotByName(name)
        }
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
            mapFragment.getMapAsync(this)
        }
    }

    private fun showParkingLotList() {
        parkingLotList.forEach { place ->
            val marker = myMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.location)
                    .icon(parkingLocationIcon)
            )
            marker?.tag = place
        }
    }

    private fun onClickSuggestItem(parkingLot: ParkingLot) {
        binding.edtSearchLocation.setText(parkingLot.name)
        binding.edtSearchLocation.setSelection(parkingLot.name.length)
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkingLot.location, 15.0F))
    }
}
