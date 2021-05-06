package com.example.transporargo.main_fragments.ui.myrequests.recyclerview

import com.example.transporargo.main_fragments.interfaces.RequestListener

class MyRequestsListener(
    private val showPlacesCallback: (String, String, String, String) -> Unit,
    private val deleteRequestCallback: (String) -> Unit,
    private val editRequestCallback: (String) -> Unit,
    private val copyRequestCallback: (String) -> Unit
) : RequestListener {

    fun showPlaces(fromPlaceLatLng: String, fromPlaceName: String, toPlaceLatLng: String, toPlaceName: String) =
        showPlacesCallback(fromPlaceLatLng, fromPlaceName, toPlaceLatLng, toPlaceName)

    fun deleteRequest(requestId: String) = deleteRequestCallback(requestId)

    fun editRequest(requestId: String) = editRequestCallback(requestId)

    fun copyRequest(requestId: String) = copyRequestCallback(requestId)
}