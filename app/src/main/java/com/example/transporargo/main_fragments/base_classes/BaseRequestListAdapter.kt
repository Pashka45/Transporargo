package com.example.transporargo.main_fragments.base_classes

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transporargo.databinding.MyRequestListItemBinding
import com.example.transporargo.main_fragments.interfaces.RequestListener
import com.example.transporargo.main_fragments.ui.Request
import com.example.transporargo.main_fragments.recyclerview.DataItem
import com.example.transporargo.main_fragments.ui.myrequests.recyclerview.MyRequestsAdapter
import com.example.transporargo.main_fragments.recyclerview.RequestsDiffCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BaseRequestListAdapter(val listener: RequestListener) {

    protected val adapterScope = CoroutineScope(Dispatchers.Default)

    abstract fun addRequests(requests: List<Request>, callback: () -> Unit)
}