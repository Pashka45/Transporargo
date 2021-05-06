package com.example.transporargo.loadingactivity

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.transporargo.main_fragments.data.local.LocalDataSource
import com.example.transporargo.main_fragments.data.local.getDatabase
import com.example.transporargo.utils.Authentication
import kotlinx.coroutines.launch

class LoadingViewModel(context: Application) : ViewModel() {

    val startAuthActivity = MutableLiveData<Boolean>()
    val startMainActivity = MutableLiveData<Boolean>()

    private val localDataSource = LocalDataSource(getDatabase(context))

    fun getUserInfo() {
        viewModelScope.launch {
            localDataSource.getUserInfo { userInfo ->
                Log.i("userInfo", userInfo.toString())
                if (userInfo == null) {
                    startAuthActivity.postValue(true)
                } else {
                    Authentication.email = userInfo.email
                    Authentication.fullName = "${userInfo.name} ${userInfo.surname}"
                    Authentication.id = userInfo.id
                    startMainActivity.postValue(true)
                }
            }
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoadingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoadingViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}