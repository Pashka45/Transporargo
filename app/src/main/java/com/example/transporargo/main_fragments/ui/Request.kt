package com.example.transporargo.main_fragments.ui

import java.io.Serializable

data class Request(
    var id: String,
    var ownerId: String,
    var cube: Float,
    var weight: Float,
    var fromPlace: String,
    var fromPlaceLatLng: String,
    var toPlace: String,
    var toPlaceLatLng: String,
    var requestType: String,
    var cargoType: String,
    var truckType: String,
    var dateEvaluation: String,
    var phone: String
) : Serializable {
    companion object {
       const val TYPE_TRANSPORT = "T"
       const val TYPE_CARGO = "C"
    }
}
