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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentLocationBinding
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.example.parkingqr.domain.model.parkinglot.ParkingLotClusterItem
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.utils.BitmapUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@RequiresApi(Build.VERSION_CODES.R)
class LocationFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        const val FINE_PERMISSION_CODE = 123
        const val METERS_TO_KILOMETERS = 1000
    }

    private lateinit var binding: FragmentLocationBinding
    private lateinit var myMap: GoogleMap
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var suggestionParkingLotAdapter: SuggestionParkingLotAdapter
    private lateinit var parkingLotList: MutableList<ParkingLot>
    private val locationViewModel: LocationViewModel by hiltNavGraphViewModels(R.id.locationFragment)
    private var circle: Circle? = null
    private var searchJob: Job? = null
    private var radiusCircle: Double = 5000.0

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
                locationViewModel.uiState.map { it.isShowNearbyParkingLots }.distinctUntilChanged()
                    .collect {
                        if (it) {
                            BottomSheetRecentDistanceListFragment().show(
                                childFragmentManager,
                                BottomSheetRecentDistanceListFragment.TAG
                            )
                            locationViewModel.showNearbyParkingLots()
                        }

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
        binding.btnFindCloseDistanceLocation.setOnClickListener {
            handleFindNearbyParkingLot()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.setOnMapClickListener {
            showSearchList(false)
            removeCircle()
        }
        LatLng(currentLocation.latitude, currentLocation.longitude).let {
            myMap.addMarker(MarkerOptions().position(it).title("My location"))
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15.0F))
        }
        addClusterMarkers(googleMap)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    }

    private fun handleFindNearbyParkingLot() {
        addCircle()
        locationViewModel.getNearbyParkingLot(
            LatLng(
                currentLocation.latitude,
                currentLocation.longitude
            ), radiusCircle / METERS_TO_KILOMETERS
        )
    }

    private fun onMarkerClick(item: ParkingLotClusterItem) {
        locationViewModel.getParkingLotInfo(item.data.id)
    }

    private fun addClusterMarkers(googleMap: GoogleMap) {
        val clusterManager = ClusterManager<ParkingLotClusterItem>(requireContext(), googleMap)
        clusterManager.renderer = ParkingLotRenderer(requireContext(), googleMap, clusterManager)
        clusterManager.markerCollection.setInfoWindowAdapter(
            MarkerInfoWindowCustomAdapter(
                requireContext()
            )
        )
        googleMap.setOnCameraIdleListener(clusterManager)
        clusterManager.setOnClusterItemClickListener { item ->
            onMarkerClick(item)
            return@setOnClusterItemClickListener false
        }
        clusterManager.addItems(parkingLotList.map { ParkingLotClusterItem(it) })
        clusterManager.cluster()
    }

    private fun addCircle() {
        circle?.remove()
        circle = myMap.addCircle(
            CircleOptions().center(LatLng(currentLocation.latitude, currentLocation.longitude))
                .radius(radiusCircle)
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.icon_color))
                .strokeWidth(2.0F)
                .fillColor(ContextCompat.getColor(requireContext(), R.color.icon_color_transparent))
        )
        myMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentLocation.latitude,
                    currentLocation.longitude
                ), 12.0F
            )
        )
    }

    private fun removeCircle() {
        circle?.remove()
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

    private fun onClickSuggestItem(parkingLot: ParkingLot) {
        binding.edtSearchLocation.setText(parkingLot.name)
        binding.edtSearchLocation.setSelection(parkingLot.name.length)
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkingLot.location, 15.0F))
    }
}
