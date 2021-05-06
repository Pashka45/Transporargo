
package com.example.transporargo.main_fragments.ui.myrequests.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.transporargo.R
import com.example.transporargo.databinding.RequestListItemBinding
import com.example.transporargo.main_fragments.recyclerview.DataItem
import com.example.transporargo.main_fragments.recyclerview.RequestsDiffCallback
import com.example.transporargo.main_fragments.ui.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RequestListAdapter(private val listener: RequestListsListener) :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(
        RequestsDiffCallback()
    ) {

    var isStart = false

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addRequests(requests: List<Request>, callback: () -> Unit) {
        if (!isStart) {
            return
        }
        Log.i("here", "called")
        adapterScope.launch {
            val items = requests.map {
                DataItem.RequestItem(it)
            }
            withContext(Dispatchers.Main) {
                submitList(items)
                callback()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) as DataItem.RequestItem

        if (holder is ViewHolder) {
            holder.bind(item.request, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    class ViewHolder(private val binding: RequestListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val TAG = "MyRequestsAdapter"

        fun bind(item: Request, listener: RequestListsListener) {
            //binding.listener = listener
            val context = binding.requestContainer.context
            binding.request = RequestListItem(
                item.id,
                String.format(
                    context.getString(R.string.request_path_template),
                    item.fromPlace,
                    item.toPlace
                ),
                item.fromPlace,
                item.toPlace,
                item.dateEvaluation,
                String.format(
                    context.getString(R.string.cube_template),
                    item.cube
                ),
                String.format(
                    context.getString(R.string.weight_template),
                    item.weight
                ),
                if (item.requestType == Request.TYPE_TRANSPORT) item.truckType else item.cargoType,
                if (item.requestType == Request.TYPE_TRANSPORT) R.drawable.outline_local_shipping_24 else R.drawable.outline_inventory_2_24,
                if (item.requestType == Request.TYPE_TRANSPORT) R.drawable.transport_border_radius else R.drawable.cargo_border_radius,
                item.fromPlaceLatLng,
                item.toPlaceLatLng,
                item.phone,
                listener
            )
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RequestListItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(
                    binding
                )
            }
        }
    }

    data class RequestListItem(
        val requestId: String,
        val requestPath: String,
        val placeFromName: String,
        val placeToName: String,
        val evaluatesDates: String,
        val cube: String,
        val weight: String,
        val truckOrCargoName: String,
        val requestImg: Int,
        val requestBorder: Int,
        val fromPlaceLatLng: String,
        val toPlaceLatLng: String,
        val phone: String,
        val listener: RequestListsListener
    )
}
