package com.example.transporargo.main_fragments.interfaces

import com.example.transporargo.main_fragments.ui.Request

interface RequestAdapter {
    fun addRequests(requests: List<Request>, callback: () -> Unit)
}