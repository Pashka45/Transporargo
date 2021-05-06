package com.example.transporargo.main_fragments.base_classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseFormViewModel: ViewModel() {

    val evaluateDate = MutableLiveData<String>()
    val weight = MutableLiveData<String>()
    val capacity = MutableLiveData<String>()
    val truckType = MutableLiveData<String>()
    val cargoType = MutableLiveData<String>()

    val placeFromName = MutableLiveData<String>()
    val placeToName = MutableLiveData<String>()
    val placeFromLatLng = MutableLiveData<String>()
    val placeToLatLng = MutableLiveData<String>()
    val isCargo = MutableLiveData<Boolean>(true)

    protected val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String>
        get() = _toastText

    fun setCargoForm() {
        isCargo.value = true
    }

    fun setTransportForm() {
        isCargo.value = false
    }
}