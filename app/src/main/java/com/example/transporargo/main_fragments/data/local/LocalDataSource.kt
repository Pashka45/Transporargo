package com.example.transporargo.main_fragments.data.local

import android.util.Log
import com.example.transporargo.main_fragments.data.DataSource
import com.example.transporargo.main_fragments.data.local.dto.MyRequestsDTO
import com.example.transporargo.main_fragments.data.local.dto.UsersInfoDTO
import com.example.transporargo.main_fragments.ui.Request
import com.example.transporargo.utils.asDatabaseModel
import com.example.transporargo.utils.asModel
import com.example.transporargo.utils.asRequestDatabaseModel
import com.example.transporargo.utils.asRequestModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSource(private val db: TransargoDatabase) : DataSource {

    suspend fun saveUserInfo(userInfo: UsersInfoDTO) {
        withContext(Dispatchers.IO) {
            db.usersInfoDao.saveUserInfo(userInfo)
        }
    }

    suspend fun getUserInfo(callback: (UsersInfoDTO?) -> Unit) {
        withContext(Dispatchers.IO) {
            val user = db.usersInfoDao.getUser()
            callback(user)
        }
    }

    suspend fun deleteUser() {
        withContext(Dispatchers.IO) {
            db.usersInfoDao.deleteUser()
        }
    }

    suspend fun addMyRequests(requests: List<Request>, callback: (List<Request>) -> Unit) {
        withContext(Dispatchers.IO) {
            db.myRequestsDao.deleteAll()
            val myRequests = requests.asDatabaseModel()
            db.myRequestsDao.addRequests(myRequests)
            val requestsFromDb = db.myRequestsDao.getRequests().asModel()
            callback(requestsFromDb)
        }
    }

    suspend fun getMyRequests(callback: (List<Request>) -> Unit) {
        withContext(Dispatchers.IO) {
            val requests = db.myRequestsDao.getRequests().asModel()
            callback(requests)
        }
    }

    suspend fun getRequests(callback: (List<Request>) -> Unit) {
        withContext(Dispatchers.IO) {
            val requests = db.requestsDao.getRequests().asRequestModel()
            callback(requests)
        }
    }

    suspend fun deleteRequest(requestId: String) {
        withContext(Dispatchers.IO) {
            db.myRequestsDao.deleteRequest(requestId)
        }
    }

    suspend fun saveRequests(requests: List<Request>, callback: (List<Request>) -> Unit) {
        withContext(Dispatchers.IO) {
            db.requestsDao.deleteAll()
            db.requestsDao.saveRequests(requests.asRequestDatabaseModel())
            callback(requests)
        }
    }
}