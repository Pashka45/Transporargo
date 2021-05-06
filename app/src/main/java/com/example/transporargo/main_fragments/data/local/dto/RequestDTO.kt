package com.example.transporargo.main_fragments.data.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "requests")
data class RequestDTO(
    @ColumnInfo(name = "owner_id") var ownerId: String,
    @ColumnInfo(name = "cube") var cube: Float,
    @ColumnInfo(name = "weight") var weight: Float,
    @ColumnInfo(name = "from_place") var fromPlace: String,
    @ColumnInfo(name = "from_place_lat_lng") var fromPlaceLatLng: String,
    @ColumnInfo(name = "to_place") var toPlace: String,
    @ColumnInfo(name = "to_place_lat_lng") var toPlaceLatLng: String,
    @ColumnInfo(name = "request_type") var requestType: String,
    @ColumnInfo(name = "cargo_type") var cargoType: String,
    @ColumnInfo(name = "truck_type") var truckType: String,
    @ColumnInfo(name = "date_evaluation") var dateEvaluation: String,
    @ColumnInfo(name = "phone") var phone: String,
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString()
)
