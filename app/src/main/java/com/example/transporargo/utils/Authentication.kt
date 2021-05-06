package com.example.transporargo.utils

import com.example.transporargo.main_fragments.data.local.dto.UsersInfoDTO

object Authentication {
    var id = ""
    var email = ""
    var fullName = ""

    fun setAuthInfo(userInfo: UsersInfoDTO) {
        id = userInfo.id
        email = userInfo.email
        fullName = "${userInfo.name} ${userInfo.surname}"
    }

    fun clear() {
        id = ""
        email = ""
        fullName = ""
    }
}