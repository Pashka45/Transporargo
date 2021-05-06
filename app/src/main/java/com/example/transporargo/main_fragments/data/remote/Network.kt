package com.example.transporargo.main_fragments.data.remote

import android.util.Log
import co.infinum.retromock.Retromock
import co.infinum.retromock.meta.Mock
import co.infinum.retromock.meta.MockResponse
import com.example.transporargo.R
import com.example.transporargo.main_fragments.data.local.dto.UsersInfoDTO
import com.example.transporargo.main_fragments.data.remote.dto.UsersInfoServerDTO
import com.example.transporargo.main_fragments.data.remote.server.ResourceBodyFactory
import com.example.transporargo.main_fragments.ui.Request
import com.example.transporargo.main_fragments.ui.SearchValues
import com.example.transporargo.utils.FileHandler
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

const val IS_EMAIL_EXISTS_PATH = "/user/is_email_exist"
const val USER_ADD_PATH = "/user/add"
const val LOGIN_USER = "/user/login"

const val REQUEST_ADD_PATH = "/request/add"
const val REQUEST_EDIT_PATH = "/request/edit"
const val REQUEST_MY_GET_PATH = "/request/get_my_requests"
const val REQUEST_DELETE_PATH = "/request/delete"

const val SEARCH_FIND_REQUEST = "/request/search"

interface Service {
    @Mock
    @MockResponse(bodyFactory = ResourceBodyFactory::class)
    @POST(IS_EMAIL_EXISTS_PATH)
    fun isEmailExists(@Query("email") email: String): Call<String>

    @Mock
    @MockResponse(bodyFactory = ResourceBodyFactory::class)
    @POST(USER_ADD_PATH)
    fun addUser(
        @Query("email") email: String, @Query("password") password: String,
        @Query("name") name: String, @Query("surname") surname: String
    ): Call<String>


    @Mock
    @MockResponse(bodyFactory = ResourceBodyFactory::class)
    @POST(REQUEST_ADD_PATH)
    fun addRequest(
        request: Request
    ): Call<String>

    @Mock
    @MockResponse(bodyFactory = ResourceBodyFactory::class)
    @POST(REQUEST_MY_GET_PATH)
    fun getMyRequests(
        ownerId: String
    ): Call<String>

    @Mock
    @MockResponse(bodyFactory = ResourceBodyFactory::class)
    @POST(REQUEST_DELETE_PATH)
    fun deleteRequest(
        requestId: String
    ): Call<String>

    @Mock
    @MockResponse(bodyFactory = ResourceBodyFactory::class)
    @POST(SEARCH_FIND_REQUEST)
    fun searchRequests(
        searchValues: SearchValues
    ): Call<String>
}

class Network(
    fileHandler: FileHandler
) {
    // Configure retrofit to getList JSON and use coroutines

    private val resourceBodyFactory = ResourceBodyFactory(fileHandler)

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl("https://site.lc")
        .build()

    private val retromock = Retromock.Builder()
        .retrofit(retrofit)
        .addBodyFactory(resourceBodyFactory)
        .build()

    private val service: Service = retromock.create(Service::class.java)

    suspend fun validateEmail(email: String): Int {
        resourceBodyFactory.queryString = "$IS_EMAIL_EXISTS_PATH?email=$email"
        val responseText = service.isEmailExists(email).await()
        val isEmailExist = JSONObject(responseText).getBoolean("isEmailExist")
        return if (isEmailExist) R.string.user_with_this_email_already_exist else 0
    }

    suspend fun login(email: String, password: String): UsersInfoDTO? {
        resourceBodyFactory.queryString = "$LOGIN_USER?email=$email&password=$password"
        val responseText = service.isEmailExists(email).await()
        val isUserExist = JSONObject(responseText).getBoolean("isUserExist")
        return if (isUserExist) {
            val info = JSONObject(responseText).getJSONObject("info")

            createUserInfoDTOInstanceByJson(info)
        } else {
            null
        }
    }


    suspend fun addUser(usersInfoDTO: UsersInfoServerDTO): UsersInfoDTO? {
        resourceBodyFactory.queryString =
            "$USER_ADD_PATH?email=${usersInfoDTO.email}&password=${usersInfoDTO.password}" +
                    "&name=${usersInfoDTO.name}&surname=${usersInfoDTO.surname}"

        val response = service.addUser(
            usersInfoDTO.email,
            usersInfoDTO.password,
            usersInfoDTO.name,
            usersInfoDTO.surname
        ).await()

        val info = JSONObject(response)

        return createUserInfoDTOInstanceByJson(info)
    }

    suspend fun deleteRequest(requestId: String): Boolean {
        resourceBodyFactory.queryString =
            "$REQUEST_DELETE_PATH?request_id=$requestId"

        val response = service.deleteRequest(
            requestId
        ).await()

        val info = JSONObject(response)

        return info.getBoolean("isRequestDeleted")
    }

    suspend fun getMyRequests(ownerId: String): List<Request>? {
        resourceBodyFactory.queryString =
            "$REQUEST_MY_GET_PATH?owner_id=${ownerId}"

        val response = service.getMyRequests(
            ownerId
        ).await()

        val requestsArray = JSONObject(response).getJSONArray("requests")
        val requestList = createRequestListByJson(requestsArray)

        return if (requestList.isEmpty())
            null
        else requestList
    }

    suspend fun addRequest(request: Request): Boolean {
        resourceBodyFactory.queryString =
            "$REQUEST_ADD_PATH?id=${request.id}&ownerId=${request.ownerId}" +
                    "&fromPlace=${request.fromPlace}&fromPlaceLatLng=${request.fromPlaceLatLng}" +
                    "&toPlace=${request.toPlace}&toPlaceLatLng=${request.toPlaceLatLng}" +
                    "&weight=${request.weight}&cube=${request.cube}" +
                    "&cargoType=${request.cargoType}&truckType=${request.truckType}" +
                    "&requestType=${request.requestType}&phone=${request.phone}" +
                    "&dateEvaluation=${request.dateEvaluation}"

        val response = service.addRequest(
            request
        ).await()

        return JSONObject(response).getBoolean("isRequestAdded")
    }

    suspend fun editRequest(request: Request): Boolean {
        resourceBodyFactory.queryString =
            "$REQUEST_EDIT_PATH?id=${request.id}&ownerId=${request.ownerId}" +
                    "&fromPlace=${request.fromPlace}&fromPlaceLatLng=${request.fromPlaceLatLng}" +
                    "&toPlace=${request.toPlace}&toPlaceLatLng=${request.toPlaceLatLng}" +
                    "&weight=${request.weight}&cube=${request.cube}" +
                    "&cargoType=${request.cargoType}&truckType=${request.truckType}" +
                    "&requestType=${request.requestType}&phone=${request.phone}" +
                    "&dateEvaluation=${request.dateEvaluation}"

        val response = service.addRequest(
            request
        ).await()

        return JSONObject(response).getBoolean("isRequestEdited")
    }


    suspend fun searchRequests(searchValues: SearchValues): List<Request> {
        resourceBodyFactory.queryString =
            "$SEARCH_FIND_REQUEST?place_from=${searchValues.placeFrom}&place_from_latlng=${searchValues.placeFromLatLng}" +
                    "&place_to=${searchValues.placeTo}&place_to_latlng=${searchValues.placeToLatLng}" +
                    "&weight=${searchValues.weight}&capacity=${searchValues.capacity}" +
                    "&cargo_type=${searchValues.cargoType}&truck_type=${searchValues.truckType}" +
                    "&evaluate_date=${searchValues.evaluateDate}&is_cargo=${searchValues.isCargo}"

        val response = service.searchRequests(
            searchValues
        ).await()

        return createRequestListByJson(JSONObject(response).getJSONArray("requests"))
    }

    private fun createRequestListByJson(requestsArray: JSONArray): List<Request> {

        return (0 until requestsArray.length()).map {
            Log.i("requestsIndex", it.toString())

            Request(
                requestsArray.getJSONObject(it).getString("id"),
                requestsArray.getJSONObject(it).getString("ownerId"),
                requestsArray.getJSONObject(it).getDouble("cube").toFloat(),
                requestsArray.getJSONObject(it).getDouble("weight").toFloat(),
                requestsArray.getJSONObject(it).getString("fromPlace"),
                requestsArray.getJSONObject(it).getString("fromPlaceLatLng"),
                requestsArray.getJSONObject(it).getString("toPlace"),
                requestsArray.getJSONObject(it).getString("toPlaceLatLng"),
                requestsArray.getJSONObject(it).getString("requestType"),
                requestsArray.getJSONObject(it).getString("cargoType"),
                requestsArray.getJSONObject(it).getString("truckType"),
                requestsArray.getJSONObject(it).getString("dateEvaluation"),
                requestsArray.getJSONObject(it).getString("phone")
            )
        }
    }

    private fun createUserInfoDTOInstanceByJson(info: JSONObject): UsersInfoDTO {
        return UsersInfoDTO(
            info.getString("id"),
            info.getString("email"),
            info.getString("name"),
            info.getString("surname")
        )
    }
}