package com.example.transporargo.main_fragments.ui.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.transporargo.R
import com.example.transporargo.main_fragments.base_classes.BaseFormViewModel
import com.example.transporargo.main_fragments.data.local.LocalDataSource
import com.example.transporargo.main_fragments.data.local.getDatabase
import com.example.transporargo.main_fragments.data.remote.RemoteDataSource
import com.example.transporargo.main_fragments.data.remote.server.EMPTY_FILED_VAL
import com.example.transporargo.main_fragments.ui.Request
import com.example.transporargo.main_fragments.ui.SearchValues
import com.example.transporargo.main_fragments.ui.myrequests.recyclerview.RequestListAdapter
import com.example.transporargo.main_fragments.ui.search.search_form.SearchValidator
import com.example.transporargo.utils.FileHandler
import com.example.transporargo.utils.SingleLiveEvent
import com.example.transporargo.utils.isNetworkAvailable
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class SearchViewModel(private val context: Application) : BaseFormViewModel() {

    private val remoteDataSource = RemoteDataSource(FileHandler(context))
    private val localDataSource = LocalDataSource(getDatabase(context))
    private val searchValues = MutableLiveData<SearchValues>()

    val foundRequests = MutableLiveData<List<Request>>()

    val showRecyclerView = MutableLiveData<Boolean>()
    var isStarted = false

    val requestsToShow = MutableLiveData<List<Request>>()

    fun isFormFillingValid(): Boolean {
        val validator =
            SearchValidator(
                evaluateDate.value,
                weight.value,
                capacity.value,
                truckType.value,
                cargoType.value,
                placeFromName.value,
                placeToName.value
            )

        if (validator.isValid()) {
            searchValues.value = SearchValues(
                if (evaluateDate.value.isNullOrEmpty()) SearchValues.EMPTY_FILED_VAL else evaluateDate.value!!,
                if (weight.value.isNullOrEmpty()) SearchValues.EMPTY_FILED_VAL else weight.value!!,
                if (capacity.value.isNullOrEmpty()) SearchValues.EMPTY_FILED_VAL else capacity.value!!,
                isCargo.value ?: true,
                if (truckType.value.isNullOrEmpty()) SearchValues.EMPTY_FILED_VAL else truckType.value!!,
                if (cargoType.value.isNullOrEmpty()) SearchValues.EMPTY_FILED_VAL else cargoType.value!!,
                if (placeFromName.value.isNullOrEmpty()) SearchValues.EMPTY_FILED_VAL else placeFromName.value!!,
                if (placeFromLatLng.value.isNullOrEmpty()) SearchValues.EMPTY_FILED_VAL else placeFromLatLng.value!!,
                if (placeToName.value.isNullOrEmpty()) SearchValues.EMPTY_FILED_VAL else placeToName.value!!,
                if (placeToLatLng.value.isNullOrEmpty()) SearchValues.EMPTY_FILED_VAL else placeToLatLng.value!!
            )
            return true
        }

        _toastText.value = validator.errorMsgId?.let {
            val error = context.getString(it)
            Log.i("errorMsgId", error)
            error
        }

        return false
    }

    private val _needToShowLoading = SingleLiveEvent<Boolean>()
    val needToShowLoading: LiveData<Boolean>
        get() = _needToShowLoading

    private val _isEmptyRequestList = MutableLiveData<Boolean>()
    val isEmptyRequestList: LiveData<Boolean>
        get() = _isEmptyRequestList

    fun getRequestListRemote() {
        isStarted = true
        _isEmptyRequestList.value = false
        _needToShowLoading.value = true
        viewModelScope.launch {
            try {
                if (!isNetworkAvailable(context)) {
                    throw UnknownHostException()
                }

                remoteDataSource.getRequestsBySearchValues(searchValues.value!!) { requests ->
                    foundRequests.postValue(requests)
                }

            } catch (e: UnknownHostException) {
                _toastText.postValue(context.getString(R.string.no_internet_connection))
                getRequestListLocal()
            }
        }
    }

    fun saveRequestList() {
        viewModelScope.launch {
            localDataSource.saveRequests(foundRequests.value!!) { requestList ->
                requestsToShow.postValue(requestList)
            }
        }
    }

    private fun getRequestListLocal() {
        viewModelScope.launch {
            localDataSource.getRequests { requestList ->
                requestsToShow.postValue(requestList)
            }
        }
    }

    fun addRequestsToAdapter(adapter: RequestListAdapter) {
        Log.i("isStarted", isStarted.toString())
        if (!isStarted) {
            return
        }

        adapter.addRequests(requestsToShow.value!!) {
            Log.i("addIsStarted", isStarted.toString())
            if (!adapter.isStart) {
                return@addRequests
            }

            _needToShowLoading.value = false
            if (requestsToShow.value!!.isEmpty()) {
                _isEmptyRequestList.value = true
            } else {
                _isEmptyRequestList.value = false
                showRecyclerView.value = true
            }
        }
    }

    fun setToastText(text: String) {
        _toastText.value = text
    }

    fun showLoader() {
        _needToShowLoading.value = true
    }

    fun hideLoader() {
        _needToShowLoading.value = false
    }

    fun clear(adapter: RequestListAdapter) {
        _toastText.value = null
        requestsToShow.value = mutableListOf()
        foundRequests.value = mutableListOf()
        showRecyclerView.value = false
        isStarted = true
        adapter.isStart = true
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}