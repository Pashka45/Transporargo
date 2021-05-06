package com.example.transporargo.main_fragments.data.remote.server

import android.util.Log
import co.infinum.retromock.BodyFactory
import com.example.transporargo.utils.FileHandler
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream

class ResourceBodyFactory(
    fileHandler: FileHandler
) : BodyFactory {

    var queryString: String = ""

    private val usersHandler = UsersHandler(fileHandler)
    private val requestHandler = RequestHandler(fileHandler)

    @Throws(IOException::class)
    override fun create(input: String): InputStream {

        val (urlPath, urlParams) = queryString.split("?")

        Log.i("queryString", queryString)
        val params: MutableMap<String, String> =
            HashMap()

        for (keyValue in urlParams.split("&")) {
            val (key, value) = keyValue.split("=")
            params[key] = value
        }

        val (_, handlerType, method) = urlPath.split("/")
        val response = when (handlerType) {
            "user" -> {
                usersHandler.doWorkByMethodAndParams(method, params)
            }
            "request" -> {
                requestHandler.doWorkByMethodAndParams(method, params)
            }
            else -> ""
        }

        Log.i("res_text", response)

        return ByteArrayInputStream(response.toByteArray())
    }


}