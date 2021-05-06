package com.example.transporargo.main_fragments.base_classes

import android.os.Bundle
import android.view.View
import com.example.transporargo.R
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

abstract class BaseSelectPlaceMapFragment: BaseMapFragment() {

    protected var isPlaceFrom = false
    protected var placeName: String? = null
    protected var latLngStr: String? = null

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        onMapReadyCallback()
        setPioClickListener()
        setClientLocationEnabled()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setPioClickListener() {
        map.setOnPoiClickListener { poi ->
            map.clear()

            placeName = poi.name
            latLngStr = "${poi.latLng.latitude}|${poi.latLng.longitude}"

            addMarker(poi.latLng, poi.name)
        }
    }

    protected abstract fun onMapReadyCallback()
}