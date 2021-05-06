package com.example.transporargo.main_fragments.data.local.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user_info")
data class UsersInfoDTO(
    @PrimaryKey @ColumnInfo(name = "id") val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "surname") var surname: String
)
