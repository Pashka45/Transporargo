package com.example.transporargo.utils

import android.widget.ImageView
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter

@BindingAdapter("backgroundBorderSet")
fun setBackgroundBorder(imageView: ImageView, resource: Int) {
    imageView.setBackgroundResource(resource)
}

@BindingAdapter("android:src")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("border")
fun setResource(view: ConstraintLayout, resource: Int) {
    view.setBackgroundResource(resource)
}
