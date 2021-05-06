package com.example.transporargo.main_fragments.recyclerview

import androidx.recyclerview.widget.DiffUtil

class RequestsDiffCallback: DiffUtil.ItemCallback<DataItem>()  {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

}