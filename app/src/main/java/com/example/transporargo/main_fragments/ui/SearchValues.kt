package com.example.transporargo.main_fragments.ui

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SearchValues(
    @SerializedName("evaluate_date") val evaluateDate: String,
    val weight: String,
    val capacity: String,
    @SerializedName("is_cargo") val isCargo: Boolean,
    @SerializedName("truck_type") val truckType: String,
    @SerializedName("cargo_type") val cargoType: String,
    @SerializedName("place_from") val placeFrom: String,
    @SerializedName("place_from_latlng") val placeFromLatLng: String,
    @SerializedName("place_to") val placeTo: String,
    @SerializedName("place_to_latlng") val placeToLatLng: String
) : Serializable {
    companion object {
        const val EMPTY_FILED_VAL = "empty"
    }
}