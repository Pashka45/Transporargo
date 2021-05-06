package com.example.transporargo.main_fragments.data.local

import androidx.room.*
import com.example.transporargo.main_fragments.data.local.dto.MyRequestsDTO
import com.example.transporargo.main_fragments.data.local.dto.RequestDTO
import com.example.transporargo.main_fragments.data.local.dto.UsersInfoDTO
import com.example.transporargo.main_fragments.ui.UserInfo
import kotlinx.coroutines.Deferred

//import com.example.transporargo.main_fragments.data.local.dto.RequestServerDTO

/**
 * Data Access Object for the reminders table.
 */
@Dao
interface RequestsDao {

    @Query("SELECT * FROM requests")
    suspend fun getRequests(): List<RequestDTO>

    @Insert
    suspend fun saveRequests(requests: List<RequestDTO>)

    @Query("DELETE FROM requests")
    suspend fun deleteAll()
}

@Dao
interface MyRequestsDao {

    @Insert
    suspend fun addRequests(requests: List<MyRequestsDTO>)

    @Query("DELETE FROM my_requests")
    suspend fun deleteAll()

    @Query("SELECT * FROM my_requests")
    suspend fun getRequests(): List<MyRequestsDTO>

    @Query("DELETE FROM my_requests WHERE id = :requestId")
    suspend fun deleteRequest(requestId: String)
}

@Dao
interface UserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserInfo(userInfo: UsersInfoDTO)

    @Query("SELECT * FROM user_info LIMIT 1")
    suspend fun getUser(): UsersInfoDTO?

    @Query("DELETE FROM user_info")
    suspend fun deleteUser()
}
