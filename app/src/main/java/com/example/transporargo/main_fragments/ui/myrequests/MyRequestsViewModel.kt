package com.example.transporargo.main_fragments.ui.myrequests

import android.app.Application
import androidx.lifecycle.*
import com.example.transporargo.R
import com.example.transporargo.main_fragments.data.local.LocalDataSource
import com.example.transporargo.main_fragments.data.local.getDatabase
import com.example.transporargo.main_fragments.data.remote.RemoteDataSource
import com.example.transporargo.main_fragments.ui.Request
import com.example.transporargo.main_fragments.ui.myrequests.recyclerview.MyRequestsAdapter
import com.example.transporargo.utils.Authentication
import com.example.transporargo.utils.FileHandler
import com.example.transporargo.utils.SingleLiveEvent
import com.example.transporargo.utils.isNetworkAvailable
import kotlinx.coroutines.launch
import java.net.UnknownHostException


class MyRequestsViewModel(private val context: Application) : ViewModel() {

    private val remoteDataSource = RemoteDataSource(FileHandler(context))
    private val localDataSource = LocalDataSource(getDatabase(context))

    private val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String>
        get() = _toastText

    private val _deletedRequestId = MutableLiveData<String>()
    val deletedRequestId: LiveData<String>
        get() = _deletedRequestId

    val myRequestList = MutableLiveData<List<Request>>()

    private val _requestsToSave = MutableLiveData<List<Request>>()
    val requestsToSave: LiveData<List<Request>>
        get() = _requestsToSave

    private val _needToShowLoading = SingleLiveEvent<Boolean>()
    val needToShowLoading: LiveData<Boolean>
        get() = _needToShowLoading

    private val _isEmptyRequestList = MutableLiveData<Boolean>()
    val isEmptyRequestList: LiveData<Boolean>
        get() = _isEmptyRequestList


    fun showRequestList() {
        _needToShowLoading.value = true
        viewModelScope.launch {
            try {
                if (!isNetworkAvailable(context)) {
                    throw UnknownHostException()
                }
                remoteDataSource.getMyRequests(Authentication.id) { requestList ->
                    _needToShowLoading.postValue(false)

                    if (requestList.isNullOrEmpty()) {
                        _isEmptyRequestList.postValue(true)
                    } else {
                        _requestsToSave.postValue(requestList)
                    }
                }
            } catch (e: UnknownHostException) {
                localDataSource.getMyRequests { requestList ->
                    _toastText.postValue(context.getString(R.string.no_internet_connection))
                    if (requestList.isNullOrEmpty()) {
                        _isEmptyRequestList.postValue(true)
                    } else {
                        myRequestList.postValue(requestList)
                    }
                }
            }
        }
    }

    fun saveRequests() {
        viewModelScope.launch {
            localDataSource.addMyRequests(_requestsToSave.value!!) { requests ->
                myRequestList.postValue(requests)
            }
        }
    }

    fun addRequestsListToAdapter(adapter: MyRequestsAdapter, requestList: List<Request>) {
        adapter.addRequests(requestList) {
            _needToShowLoading.value = false
            if (requestList.isEmpty()) {
                _isEmptyRequestList.value = true
            }
        }
    }

    fun refreshRequestsListToAdapter(adapter: MyRequestsAdapter) {
        adapter.addRequests(myRequestList.value!!) {
            _needToShowLoading.value = false
            if (myRequestList.value!!.isEmpty()) {
                _isEmptyRequestList.value = true
            }
        }
    }

    fun deleteRequestRemote(requestId: String) {
        _needToShowLoading.value = true
        viewModelScope.launch {
            remoteDataSource.deleteRequest(requestId) { isDeleted ->
                if (isDeleted) {
                    _deletedRequestId.postValue(requestId)
                } else {
                    _toastText.postValue(context.getString(R.string.smth_went_wrong_try_later))
                }
            }
        }
    }

    fun deleteRequestLocal(requestId: String, callback: (String) -> Unit) {
        viewModelScope.launch {
            localDataSource.deleteRequest(requestId)
            callback(requestId)
            _needToShowLoading.postValue(false)
        }
    }

    fun setToastText(text: String) {
        _toastText.value = text
    }

    fun clearDeletedRequestId() {
        _deletedRequestId.value = ""
    }

    fun getRequestById(requestId: String): Request {
        return myRequestList.value!!.find {
            it.id == requestId
        }!!
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MyRequestsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MyRequestsViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
