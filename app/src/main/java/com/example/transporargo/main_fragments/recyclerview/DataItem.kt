package com.example.transporargo.main_fragments.recyclerview

import com.example.transporargo.main_fragments.ui.Request

sealed class DataItem {
    data class RequestItem(val request: Request): DataItem() {
        override val id = request.id
    }

    abstract val id: String
}