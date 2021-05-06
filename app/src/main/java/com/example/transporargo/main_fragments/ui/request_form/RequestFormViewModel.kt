package com.example.transporargo.main_fragments.ui.request_form

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.transporargo.R
import com.example.transporargo.main_fragments.base_classes.BaseFormViewModel
import com.example.transporargo.main_fragments.data.local.LocalDataSource
import com.example.transporargo.main_fragments.data.local.getDatabase
import com.example.transporargo.main_fragments.data.remote.RemoteDataSource
import com.example.transporargo.main_fragments.ui.Request
import com.example.transporargo.utils.Authentication
import com.example.transporargo.utils.Data
import com.example.transporargo.utils.FileHandler
import kotlinx.coroutines.launch
import java.util.*

class RequestFormViewModel(private val context: Application) : BaseFormViewModel() {

    private val remoteDataSource = RemoteDataSource(FileHandler(context))
    private val localDataSource = LocalDataSource(getDatabase(context))

    private val _requestId = MutableLiveData<String>()
    val phone = MutableLiveData<String?>()

    private val _showLoader = MutableLiveData<Boolean>()
    val showLoader: LiveData<Boolean>
        get() = _showLoader

    private val _isRequestAdded = MutableLiveData<Boolean>()
    val isRequestAdded: LiveData<Boolean>
        get() = _isRequestAdded

    var manipulationType = RequestManipulations.ADD

    fun validateRequest(): Boolean {
        if (weight.value != null) {
            weight.value = weight.value!!.replace(",", ".")
        }

        if (capacity.value != null) {
            capacity.value = capacity.value!!.replace(",", ".")
        }

        val requestValidator = RequestValidator(
            evaluateDate.value,
            weight.value,
            capacity.value,
            truckType.value,
            cargoType.value,
            placeFromName.value,
            placeToName.value,
            phone.value,
            isCargo.value!!
        )

        if (requestValidator.isValid()) {
            setRequestIdIfEmpty()
            return true
        }

        _toastText.value = requestValidator.errorMsgId?.let {
            val error = context.getString(it)
            Log.i("errorMsgId", error)
            error
        }
        return false
    }

    private fun clear() {
        evaluateDate.postValue(null)
        capacity.postValue(null)
        weight.postValue(null)
        truckType.postValue(null)
        cargoType.postValue(null)
        placeFromName.postValue(null)
        placeFromLatLng.postValue(null)
        placeToLatLng.postValue(null)
        placeToName.postValue(null)
        placeFromLatLng.postValue(null)
        phone.postValue(null)
        manipulationType = RequestManipulations.ADD
    }

    fun manipulateWithRequest() {
        when (manipulationType) {
            RequestManipulations.ADD -> {
                addRequest()
            }
            RequestManipulations.EDIT -> {
                editRequest()
            }
        }
    }

    private fun addRequest() {
        _showLoader.postValue(true)
        val request = createNewRequestInstance()
        viewModelScope.launch {
            remoteDataSource.addRequest(request) { isAdded ->
                _showLoader.postValue(false)
                if (isAdded) {
                    _isRequestAdded.postValue(true)
                    clear()
                } else {
                    _toastText.postValue(context.getString(R.string.smth_went_wrong_try_later))
                }
            }
        }
    }

    private fun editRequest() {
        _showLoader.postValue(true)
        val request = createNewRequestInstance()
        viewModelScope.launch {
            remoteDataSource.editRequest(request) { isEdited ->
                _showLoader.postValue(false)
                if (isEdited) {
                    _isRequestAdded.postValue(true)
                    clear()
                } else {
                    _toastText.postValue(context.getString(R.string.smth_went_wrong_try_later))
                }
            }
        }
    }


    fun fillRequestFields(request: Request, needNewId: Boolean = false) {
        _requestId.value = if (needNewId) {
            generateId()
        } else {
            request.id
        }

        placeToName.value = request.toPlace
        placeFromName.value = request.fromPlace
        placeToLatLng.value = request.toPlaceLatLng
        placeFromLatLng.value = request.fromPlaceLatLng
        weight.value = request.weight.toString()
        capacity.value = request.cube.toString()
        cargoType.value = request.cargoType
        truckType.value = request.truckType
        evaluateDate.value = request.dateEvaluation
        phone.value = request.phone
        isCargo.value = request.requestType == Request.TYPE_CARGO
    }

    private fun setRequestIdIfEmpty() {
        if (_requestId.value != null) {
            return
        }
        _requestId.value = generateId()
    }

    private fun generateId(): String {
        val cal = Calendar.getInstance()
        cal.time = Date()
        return cal.timeInMillis.toString()
    }

    enum class RequestManipulations {
        ADD,
        EDIT,
    }

    private fun createNewRequestInstance(): Request {
        return Request(
            _requestId.value.toString(),
            Authentication.id,
            capacity.value!!.toFloat(),
            weight.value!!.toFloat(),
            placeFromName.value!!,
            placeFromLatLng.value!!,
            placeToName.value!!,
            placeToLatLng.value!!,
            if (isCargo.value!!) Request.TYPE_CARGO else Request.TYPE_TRANSPORT,
            if (isCargo.value!!) cargoType.value!! else "",
            if (isCargo.value!!) "" else truckType.value!!,
            evaluateDate.value!!,
            phone.value!!
        )
    }

    private fun refreshRequestData(requestOld: Request) {
        requestOld.toPlace = placeToName.value!!
        requestOld.fromPlace = placeFromName.value!!
        requestOld.toPlaceLatLng = placeToLatLng.value!!
        requestOld.fromPlaceLatLng = placeFromLatLng.value!!
        requestOld.weight = weight.value!!.toFloat()
        requestOld.cube = capacity.value!!.toFloat()
        requestOld.cargoType = cargoType.value!!
        requestOld.truckType = truckType.value!!
        requestOld.dateEvaluation = evaluateDate.value!!
        requestOld.phone = phone.value!!
        requestOld.requestType = if (isCargo.value!!) Request.TYPE_CARGO else Request.TYPE_TRANSPORT
    }

    fun showLoader() {
        _showLoader.value = true
    }

    fun hideLoader() {
        _showLoader.value = false
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RequestFormViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RequestFormViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}