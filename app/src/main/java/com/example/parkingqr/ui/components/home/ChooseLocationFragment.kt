package com.example.parkingqr.ui.components.home

import android.content.pm.PackageManager
import android.location.Location
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.example.parkingqr.R
import com.example.parkingqr.databinding.FragmentChooseLocationBinding
import com.example.parkingqr.ui.base.BaseFragment
import com.example.parkingqr.ui.components.location.LocationFragment
import com.example.parkingqr.ui.components.location.MarkerInfoWindowAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class ChooseLocationFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentChooseLocationBinding
    private lateinit var myMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var curMarker: Marker? = null
    private val viewModel: HomeViewModel by hiltNavGraphViewModels(R.id.homeFragment)


    override fun observeViewModel() {

    }

    override fun initViewBinding(): View {
        binding = FragmentChooseLocationBinding.inflate(layoutInflater)
        mapFragment =
            childFragmentManager.findFragmentById(R.id.fctvGoogleMapChooseLocation) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    override fun initListener() {
        showActionBar(getString(R.string.choose_location_fragment_name))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap.setOnMapClickListener {
            addMarker(it)
        }
        myMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireActivity()))
        viewModel.uiState.value.curMarker?.let {
            addMarker(it.position)
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it.position, 15.0F))
        }
    }

    private fun addMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng).title("Location")
        curMarker?.remove()
        curMarker = myMap.addMarker(markerOptions)
        curMarker?.let {
            viewModel.setCurMarker(it)
        }
    }

}