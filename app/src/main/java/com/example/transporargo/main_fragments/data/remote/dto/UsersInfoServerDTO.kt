package com.example.transporargo.main_fragments.data.remote.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user_info")
data class UsersInfoServerDTO(
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "surname") var surname: String,
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString()
)
