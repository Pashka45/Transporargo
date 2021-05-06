package com.example.transporargo.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import com.example.transporargo.main_fragments.data.local.dto.MyRequestsDTO
import com.example.transporargo.main_fragments.data.local.dto.RequestDTO
import com.example.transporargo.main_fragments.ui.Request
import com.google.android.gms.maps.model.LatLng

fun ObjectAnimator.disableViewDuringAnimation(view: View) {

    // This extension method listens for start/end events on an animation and disables
    // the given view for the entirety of that animation.

    addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            view.isEnabled = false
        }

        override fun onAnimationEnd(animation: Animator?) {
            view.isEnabled = true
        }
    })
}

//animate changing the view visibility
fun View.fadeIn() {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeIn.alpha = 1f
        }
    })
}

//animate changing the view visibility
fun View.fadeOut() {
    this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeOut.alpha = 1f
            this@fadeOut.visibility = View.GONE
        }
    })
}

fun List<Request>.asDatabaseModel(): List<MyRequestsDTO> {
    return map { request ->
        MyRequestsDTO (
            request.ownerId,
            request.cube,
            request.weight,
            request.fromPlace,
            request.fromPlaceLatLng,
            request.toPlace,
            request.toPlaceLatLng,
            request.requestType,
            request.cargoType,
            request.truckType,
            request.dateEvaluation,
            request.phone,
            request.id
        )
    }
}

fun List<Request>.asRequestDatabaseModel(): List<RequestDTO> {
    return map { request ->
        RequestDTO (
            request.ownerId,
            request.cube,
            request.weight,
            request.fromPlace,
            request.fromPlaceLatLng,
            request.toPlace,
            request.toPlaceLatLng,
            request.requestType,
            request.cargoType,
            request.truckType,
            request.dateEvaluation,
            request.phone,
            request.id
        )
    }
}

fun List<MyRequestsDTO>.asModel(): List<Request> {
    return map { myRequest ->
        Request (
            myRequest.id,
            myRequest.ownerId,
            myRequest.cube,
            myRequest.weight,
            myRequest.fromPlace,
            myRequest.fromPlaceLatLng,
            myRequest.toPlace,
            myRequest.toPlaceLatLng,
            myRequest.requestType,
            myRequest.cargoType,
            myRequest.truckType,
            myRequest.dateEvaluation,
            myRequest.phone
        )
    }
}

fun List<RequestDTO>.asRequestModel(): List<Request> {
    return map { request ->
        Request (
            request.id,
            request.ownerId,
            request.cube,
            request.weight,
            request.fromPlace,
            request.fromPlaceLatLng,
            request.toPlace,
            request.toPlaceLatLng,
            request.requestType,
            request.cargoType,
            request.truckType,
            request.dateEvaluation,
            request.phone
        )
    }
}