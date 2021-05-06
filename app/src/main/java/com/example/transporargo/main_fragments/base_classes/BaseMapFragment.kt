package com.example.transporargo.main_fragments.base_classes

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


abstract class BaseMapFragment: Fragment() {

    protected lateinit var map: GoogleMap
    protected val markers = mutableListOf<Marker>()
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        textSize = 60f
        style = Paint.Style.FILL_AND_STROKE
    }

    protected fun addMarker(latLng: LatLng, name: String) {
        val marker = map.addMarker(
            MarkerOptions().position(latLng).title(name).icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_VIOLET
                )
            )
        )
        markers.add(marker)
        marker.showInfoWindow()
    }

    protected fun parseLatLngString(latLngStr: String): LatLng {
        val (lat, lng) = latLngStr.split("|")
        return LatLng(lat.toDouble(), lng.toDouble())
    }

    protected fun setClientLocationEnabled() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}