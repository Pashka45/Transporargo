package com.example.transporargo.main_fragments.data.remote.server

import android.util.Log
import com.example.transporargo.main_fragments.data.remote.dto.RequestServerDTO
import com.example.transporargo.utils.FileHandler
import com.google.gson.Gson
import org.json.JSONObject

private const val TYPE_TRANSPORT = "T"
private const val TYPE_CARGO = "C"
const val EMPTY_FILED_VAL = "empty"

class RequestHandler(
    fileHandler: FileHandler
) : Handler {

    private val _fileHandler = fileHandler.clone() as FileHandler
    private val requestList = ArrayList<RequestServerDTO>()

    init {
        _fileHandler.defaultText = "{\"requests\": []}"
    }

    override val fileName = "requests.json"

    override fun doWorkByMethodAndParams(method: String, params: Map<String, String>): String {
        getList()
        Log.i("method", method)
        val result = when (method) {
            "add" -> {
                addRequest(params)
                "{\"isRequestAdded\": true}"
            }
            "edit" -> {
                editRequest(params)
                "{\"isRequestEdited\": true}"
            }
            "delete" -> {
                deleteRequest(params)
                "{\"isRequestDeleted\": true}"
            }
            "get_my_requests" -> {
                val myRequests = getMyRequests(params)

                if (myRequests == null) {
                    "{\"requests\": []}"
                } else {
                    val jsonObject = Gson().toJson(myRequests)
                    "{\"requests\": $jsonObject}"
                }
            }
            "search" -> {
                val requests = searchRequests(params)

                if (requests == null) {
                    "{\"requests\": []}"
                } else {
                    val jsonObject = Gson().toJson(requests)
                    "{\"requests\": $jsonObject}"
                }
            }
            else -> ""
        }
        saveList()
        return result
    }

    override fun getList() {
        val json = JSONObject(_fileHandler.readFile(fileName))
        val requests = json.getJSONArray("requests")
        Log.i("get.requests", requests.toString())
        (0 until requests.length()).mapTo(destination = requestList) {
            RequestServerDTO(
                requests.getJSONObject(it).getString("ownerId"),
                requests.getJSONObject(it).getDouble("cube").toFloat(),
                requests.getJSONObject(it).getDouble("weight").toFloat(),
                requests.getJSONObject(it).getString("fromPlace"),
                requests.getJSONObject(it).getString("fromPlaceLatLng"),
                requests.getJSONObject(it).getString("toPlace"),
                requests.getJSONObject(it).getString("toPlaceLatLng"),
                requests.getJSONObject(it).getString("requestType"),
                requests.getJSONObject(it).getString("cargoType"),
                requests.getJSONObject(it).getString("truckType"),
                requests.getJSONObject(it).getString("dateEvaluation"),
                requests.getJSONObject(it).getString("phone"),
                requests.getJSONObject(it).getString("id")
            )
        }
    }

    private fun addRequest(params: Map<String, String>) {
        Log.i("addInHandler", "called")
        requestList.add(
            RequestServerDTO(
                params["ownerId"]!!,
                params["cube"]!!.toFloat(),
                params["weight"]!!.toFloat(),
                params["fromPlace"]!!,
                params["fromPlaceLatLng"]!!,
                params["toPlace"]!!,
                params["toPlaceLatLng"]!!,
                params["requestType"]!!,
                params["cargoType"]!!,
                params["truckType"]!!,
                params["dateEvaluation"]!!,
                params["phone"]!!,
                params["id"]!!
            )
        )
    }

    private fun editRequest(params: Map<String, String>) {
        var found: RequestServerDTO
        requestList.forEachIndexed { index, request ->
            Log.i("found_by_id", request.id + " == " + params["id"])
            if (request.id == params["id"]) {
                requestList[index] = RequestServerDTO(
                    params["ownerId"]!!,
                    params["cube"]!!.toFloat(),
                    params["weight"]!!.toFloat(),
                    params["fromPlace"]!!,
                    params["fromPlaceLatLng"]!!,
                    params["toPlace"]!!,
                    params["toPlaceLatLng"]!!,
                    params["requestType"]!!,
                    params["cargoType"]!!,
                    params["truckType"]!!,
                    params["dateEvaluation"]!!,
                    params["phone"]!!,
                    params["id"]!!
                )
                found = requestList[index]
                Log.i("found_request", found.toString())
                return@forEachIndexed
            }
        }
    }

    private fun deleteRequest(params: Map<String, String>) {
        val requestToDelete = requestList.find {
            it.id == params["request_id"]
        }
        requestList.remove(requestToDelete)
    }

    private fun getMyRequests(params: Map<String, String>): List<RequestServerDTO>? {
        val requestFiltered = requestList.filter {
            it.ownerId == params["owner_id"]
        }

        Log.i("requestFiltered", requestFiltered.toString())

        return requestFiltered
    }

    private fun searchRequests(params: Map<String, String>): List<RequestServerDTO>? {
        return requestList.filter { request ->
            if (params["is_cargo"] != null && params["is_cargo"]!!.toBoolean() && request.requestType != TYPE_CARGO) {
                Log.i("1_if", params["is_cargo"].toString() + " " + request.requestType)
                return@filter false
            }

            if (params["is_cargo"] != null && !params["is_cargo"]!!.toBoolean() && request.requestType != TYPE_TRANSPORT) {
                Log.i("2_if", params["is_cargo"].toString() + " " + request.requestType)
                return@filter false
            }

            if (params["evaluate_date"] != null && params["evaluate_date"] != EMPTY_FILED_VAL && request.dateEvaluation != params["evaluate_date"]) {
                Log.i("3_if", params["evaluate_date"].toString() + " " + request.dateEvaluation)
                return@filter false
            }

            if (params["weight"] != null && params["weight"] != EMPTY_FILED_VAL) {
                Log.i("4_if", params["weight"].toString())
                val weightSearched = params["weight"]?.toFloatOrNull()
                if (weightSearched != null && request.weight != weightSearched) {
                    return@filter false
                }
            }

            if (params["capacity"] != null && params["capacity"] != EMPTY_FILED_VAL) {
                Log.i("5_if", params["capacity"].toString())
                val capacitySearched = params["capacity"]?.toFloatOrNull()
                if (capacitySearched != null && request.cube != capacitySearched) {
                    return@filter false
                }
            }

            if (!params["is_cargo"]!!.toBoolean() && params["truck_type"] != null && params["truck_type"] != EMPTY_FILED_VAL && request.truckType != params["truck_type"]) {
                Log.i("6_if", params["is_cargo"]!!.toBoolean().toString() + " " + (params["truck_type"] != null).toString()
                + " " + request.truckType)
                return@filter false
            }

            if (params["is_cargo"]!!.toBoolean() && params["cargo_type"] != null && params["cargo_type"] != EMPTY_FILED_VAL && request.cargoType != params["cargo_type"]) {
                Log.i("7_if", params["is_cargo"]!!.toBoolean().toString() + " " + (params["cargo_type"] != null).toString()
                        + " " + request.cargoType)
                return@filter false
            }

            if (params["place_from"] != null && params["place_from"] != EMPTY_FILED_VAL && request.fromPlace != params["place_from"]) {
                Log.i("9_if", request.fromPlaceLatLng +  " != " + params["place_from"])
                return@filter false
            }

            if (params["place_from_latlng"] != null && params["place_from_latlng"] != EMPTY_FILED_VAL && request.fromPlaceLatLng != params["place_from_latlng"]) {
                Log.i("10_if", "open")
                return@filter false
            }

            if (params["place_to"] != null && params["place_to"] != EMPTY_FILED_VAL && request.toPlace != params["place_to"]) {
                Log.i("11_if", "open")
                return@filter false
            }

            if (params["place_to_latlng"] != null && params["place_to_latlng"] != EMPTY_FILED_VAL && request.toPlaceLatLng != params["place_to_latlng"]) {
                Log.i("12_if", "open")
                return@filter false
            }

            true
        }
    }

    override fun saveList() {
        val jsonArray = Gson().toJson(requestList)
        Log.i("jsonArray", jsonArray.toString())
        _fileHandler.writeFile(fileName, "{\"requests\": $jsonArray}")
        requestList.clear()
    }
}