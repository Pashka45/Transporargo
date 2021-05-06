package com.example.transporargo.main_fragments.interfaces

interface Validator {
    var errorMsgId: Int?
    fun isValid(): Boolean
}