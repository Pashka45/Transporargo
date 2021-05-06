package com.example.transporargo.main_fragments.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.transporargo.main_fragments.data.local.dto.MyRequestsDTO
import com.example.transporargo.main_fragments.data.local.dto.RequestDTO
import com.example.transporargo.main_fragments.data.local.dto.UsersInfoDTO


@Database(entities = [RequestDTO::class, MyRequestsDTO::class, UsersInfoDTO::class], version = 7)
abstract class TransargoDatabase : RoomDatabase() {
    abstract val requestsDao: RequestsDao
    abstract val myRequestsDao: MyRequestsDao
    abstract val usersInfoDao: UserInfoDao

    companion object {
        const val dbname = "transargo"
    }
}

private lateinit var INSTANCE: TransargoDatabase

fun getDatabase(context: Context): TransargoDatabase {
    synchronized(TransargoDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(context.applicationContext, TransargoDatabase::class.java, TransargoDatabase.dbname)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}

val MIGRATION_5_7: Migration = object : Migration(5, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "DROP TABLE requests;" +
                    "CREATE TABLE requests(" +
                    "owner_id TEXT," +
                    "cube FLOAT," +
                    "weight FLOAT," +
                    "from_place TEXT," +
                    "from_place_lat_lng TEXT," +
                    "to_place TEXT," +
                    "to_place_lat_lng TEXT," +
                    "request_type TEXT," +
                    "cargo_type TEXT," +
                    "truck_type TEXT," +
                    "date_evaluation TEXT," +
                    "phone TEXT" +
                    "id TEXT," +
                    "PRIMARY KEY(id)" +
                    ")"
        )
    }
}

