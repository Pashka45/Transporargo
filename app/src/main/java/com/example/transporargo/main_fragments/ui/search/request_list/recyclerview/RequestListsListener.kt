package com.example.transporargo.main_fragments.ui.myrequests.recyclerview

import com.example.transporargo.main_fragments.interfaces.RequestListener

class RequestListsListener(
    val showPlacesCallback: (String, String, String, String) -> Unit,
    val callToPhoneCallback: (String) -> Unit
) {

    fun showPlaces(fromPlaceLatLng: String, fromPlaceName: String, toPlaceLatLng: String, toPlaceName: String) =
        showPlacesCallback(fromPlaceLatLng, fromPlaceName, toPlaceLatLng, toPlaceName)

    fun callToPhone(phone: String) = callToPhoneCallback(phone)
}
