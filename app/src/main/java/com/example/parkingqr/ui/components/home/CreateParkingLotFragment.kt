package com.example.parkingqr.ui.components.home

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentCreateParkingLotBinding
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.location.LocationFragment
import com.example.parkingqr.ui.components.location.MarkerInfoWindowAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class CreateParkingLotFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        const val REQUEST_CAMERA_PERMISSION_CODE = 123
        const val IMAGE_CODE = 456
    }

    private lateinit var binding: FragmentCreateParkingLotBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var myMap: GoogleMap
    private lateinit var imageAdapter: ImageParkingLotAdapter
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var initLocation: Location
    private val viewModel: HomeViewModel by hiltNavGraphViewModels(R.id.homeFragment)
    private var curMarker: Marker? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK) {
            data?.data?.let {
                imageAdapter.addToList(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageAdapter = ImageParkingLotAdapter(mutableListOf(null)) {
            addImage()
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.isLoading) showLoading() else hideLoading()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.map { it.isCreatedParkingLot }.distinctUntilChanged()
                    .collect { created ->
                        if (created) {
                            viewModel.resetIsCreatedPara()
                            getNavController().popBackStack()
                        }
                    }
            }
        }
    }

    override fun initViewBinding(): View {
        binding = FragmentCreateParkingLotBinding.inflate(layoutInflater)
        mapFragment =
            childFragmentManager.findFragmentById(R.id.fctvGoogleMapCreateParkingLot) as SupportMapFragment
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.rlvImageCreateParkingLot.apply {
            adapter = imageAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.create_parking_lot_fragment_name))
        getLastLocation()
        binding.btnCreateParkingLot.setOnClickListener {
            createParkingLot()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.setOnMapClickListener {
            getNavController().navigate(R.id.chooseLocationFragment)
        }
        myMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireActivity()))
        val storedMarker = viewModel.uiState.value.curMarker
        if (storedMarker != null) {
            addMarker(storedMarker.position)
        } else {
            addMarker(LatLng(initLocation.latitude, initLocation.longitude))
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
                LocationFragment.FINE_PERMISSION_CODE
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            initLocation = location
            mapFragment.getMapAsync(this)
        }
    }

    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng).title("Location")
        curMarker?.remove()
        curMarker = myMap.addMarker(markerOptions)
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0F))
        curMarker?.let {
            viewModel.setCurMarker(it)
        }
    }

    private fun addImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION_CODE
            )
        } else {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(i, IMAGE_CODE)
        }
    }

    private fun createParkingLot() {
        val name = binding.edtNameCreateParkingLot.text.toString()
        val address = binding.edtAddressCreateParkingLot.text.toString()
        val area = binding.edtAreaCreateParkingLot.text.toString()
        val carCapacity = binding.edtCarCapaCrateParkingLot.text.toString()
        val motorCapacity = binding.edtMotorCapaCreateParkingLot.text.toString()
        val description = binding.edtDescripCreateParkingLot.text.toString()
        val phone = binding.edtPhoneCreateParkingLot.text.toString()
        val images = imageAdapter.getData()
        curMarker?.position?.let { latLng ->
            LatLng(latLng.latitude, latLng.latitude)
            viewModel.createParkingLotThenUpdateParkingLotManager(
                name,
                address,
                phone,
                area,
                carCapacity,
                motorCapacity,
                description,
                images,
                latLng
            )
        }
    }
}