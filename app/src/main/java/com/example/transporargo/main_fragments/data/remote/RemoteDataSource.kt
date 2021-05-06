package com.example.transporargo.main_fragments.data.remote

import android.util.Log
import com.example.transporargo.main_fragments.data.DataSource
import com.example.transporargo.main_fragments.data.local.dto.UsersInfoDTO
import com.example.transporargo.main_fragments.data.remote.dto.UsersInfoServerDTO
import com.example.transporargo.main_fragments.ui.Request
import com.example.transporargo.main_fragments.ui.SearchValues
import com.example.transporargo.main_fragments.ui.UserInfo
import com.example.transporargo.utils.FileHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RemoteDataSource(
    fileHandler: FileHandler
): DataSource {

    private val network = Network(fileHandler)

    suspend fun addUser(usersInfoServerDTO: UsersInfoServerDTO, callback: (UsersInfoDTO?) -> Unit) {
        withContext(Dispatchers.IO) {
            val userInfo = network.addUser(usersInfoServerDTO)
            Log.i("userInfo", userInfo.toString())
            callback(userInfo)
        }
    }

    suspend fun validateEmail(email: String, callback: (Int) -> Unit) {
        withContext(Dispatchers.IO) {
            val errorStringId = network.validateEmail(email)
            callback(errorStringId)
        }
    }

    suspend fun login(email: String, password: String, callback: (UsersInfoDTO?) -> Unit) {
        withContext(Dispatchers.IO) {
            val userInfo = network.login(email, password)
            callback(userInfo)
        }
    }

    suspend fun addRequest(request: Request, callback: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val isAdded = network.addRequest(request)
            callback(isAdded)
        }
    }

    suspend fun editRequest(request: Request, callback: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val isEdited = network.editRequest(request)
            callback(isEdited)
        }
    }


    suspend fun getMyRequests(ownerId: String, callback: (List<Request>?) -> Unit) {
        withContext(Dispatchers.IO) {
            val requests = network.getMyRequests(ownerId)
            callback(requests)
        }
    }

    suspend fun deleteRequest(requestId: String, callback: (Boolean) -> Unit) {
        withContext(Dispatchers.IO) {
            val isDeleted = network.deleteRequest(requestId)
            callback(isDeleted)
        }
    }

    suspend fun getRequestsBySearchValues(searchValues: SearchValues, callback: (List<Request>) -> Unit) {
        withContext(Dispatchers.IO) {
            val requestList = network.searchRequests(searchValues)
            Log.i("requestList", requestList.toString())
            callback(requestList)
        }
    }
}