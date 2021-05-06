package com.example.transporargo.main_fragments.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.transporargo.R
import com.example.transporargo.main_fragments.MainActivity
import com.example.transporargo.main_fragments.base_classes.BaseMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import java.text.DecimalFormat
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class RequestPlacesFragment : BaseMapFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val actionBarTitle = context?.getString(R.string.request_places)
        activity?.let { activity ->
            (activity as MainActivity).setActionBarTitle(actionBarTitle.toString())
        }
        super.onCreate(savedInstanceState)
    }

    private val callback = OnMapReadyCallback { map ->
        this.map = map

        val args =
            RequestPlacesFragmentArgs.fromBundle(
                requireArguments()
            )

        val fromPlace = parseLatLngString(args.fromPlaceLatLngStr)
        val toPlace = parseLatLngString(args.toPlaceLatLngStr)

        addMarker(fromPlace, args.fromPlaceName)
        addMarker(toPlace, args.toPlaceName)
        setClientLocationEnabled()
        calculationByDistance(fromPlace, toPlace)

        val builder = LatLngBounds.Builder()
        for (marker in markers) {
            builder.include(marker.position)
        }
        val bounds = builder.build()

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 100)
        map.moveCamera(cu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun calculationByDistance(StartP: LatLng, EndP: LatLng): Double {
        val radius = 6371 // radius of earth in Km
        val lat1 = StartP.latitude
        val lat2 = EndP.latitude
        val lon1 = StartP.longitude
        val lon2 = EndP.longitude
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = (sin(dLat / 2) * sin(dLat / 2)
                + (cos(Math.toRadians(lat1))
                * cos(Math.toRadians(lat2)) * sin(dLon / 2)
                * sin(dLon / 2)))
        val c = 2 * asin(sqrt(a))
        val valueResult = radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec: Int = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
        Log.i(
            "Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec
        )
        return radius * c
    }
}