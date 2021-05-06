package com.example.transporargo.main_fragments.data.remote.server

import org.json.JSONObject

interface Handler {

    val fileName: String

    fun doWorkByMethodAndParams(method: String, params: Map<String, String>): String
    fun getList()
    fun saveList()
}
