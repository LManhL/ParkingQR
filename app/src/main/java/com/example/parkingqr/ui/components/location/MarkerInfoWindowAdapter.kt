package com.example.parkingqr.ui.components.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.parkingqr.R
import com.example.parkingqr.domain.model.parkinglot.ParkingLot
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {
        // 1. Get tag
        val place = marker.tag as? ParkingLot ?: return null
        // 2. Inflate view and set title, address, and rating
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_marker_info_content, null
        )
        view.findViewById<TextView>(
            R.id.tvTitleMarkerInfoContent
        ).text = place.name
        view.findViewById<TextView>(
            R.id.tvAddressMarkerInfoContent
        ).text = place.address

        return view
    }

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }
}