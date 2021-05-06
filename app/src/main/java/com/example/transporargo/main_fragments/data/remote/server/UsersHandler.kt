package com.example.transporargo.main_fragments.data.remote.server

import android.util.Log
import com.example.transporargo.main_fragments.data.remote.dto.UsersInfoServerDTO
import com.example.transporargo.utils.FileHandler
import com.google.gson.Gson
import org.json.JSONObject


class UsersHandler(
    fileHandler: FileHandler
) : Handler {

    private val _fileHandler = fileHandler.clone() as FileHandler

    init {
        _fileHandler.defaultText = "{\"users\": []}"
    }

    private val usersList = ArrayList<UsersInfoServerDTO>()

    override fun doWorkByMethodAndParams(method: String, params: Map<String, String>): String {
        getList()
        Log.i("method", method)
        val result = when (method) {
            "add" -> {
                Gson().toJson(addUser(params))
            }
            "is_email_exist" -> {
                val isEmailExist = isEmailExist(params)

                if (isEmailExist)
                    "{\"isEmailExist\": true}"
                else
                    "{\"isEmailExist\": false}"
            }
            "login" -> {
                val userInfo = login(params)

                if (userInfo == null) {
                    "{\"isUserExist\": false}"
                } else {
                    val jsonObject = Gson().toJson(userInfo)
                    "{\"isUserExist\": true, \"info\": $jsonObject}"
                }
            }
            "delete" -> {
                ""
            }
            "edit" -> {
                ""
            }
            else -> {
                ""
            }
        }
        saveList()

        return result
    }

    override val fileName = "users.json"

    override fun getList() {
        Log.i("getList", _fileHandler.readFile(fileName))
        val json = JSONObject(_fileHandler.readFile(fileName))

        val users = json.getJSONArray("users")
        (0 until users.length()).mapTo(destination = usersList) {
            UsersInfoServerDTO(
                users.getJSONObject(it).getString("email"),
                users.getJSONObject(it).getString("password"),
                users.getJSONObject(it).getString("name"),
                users.getJSONObject(it).getString("surname"),
                users.getJSONObject(it).getString("id")
            )
        }

    }

    override fun saveList() {
        val jsonArray = Gson().toJson(usersList)
        usersList.clear()
        _fileHandler.writeFile(fileName, "{\"users\": $jsonArray}")
    }

    private fun addUser(params: Map<String, String>): UsersInfoServerDTO {
        val user = UsersInfoServerDTO(
            params["email"]!!,
            params["password"]!!,
            params["name"]!!,
            params["surname"]!!
        )
        usersList.add(user)

        return user
    }

    private fun isEmailExist(params: Map<String, String>): Boolean {
        val userWithEmail = usersList.filter {
            it.email == params["email"]
        }

        return userWithEmail.isNotEmpty()
    }

    private fun login(params: Map<String, String>): UsersInfoServerDTO? {
        val userInfo = usersList.find {
            it.email == params["email"] && it.password == params["password"]
        }

        return userInfo?.copy()
    }
}