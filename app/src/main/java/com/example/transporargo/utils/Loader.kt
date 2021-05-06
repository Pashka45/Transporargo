package com.example.transporargo.utils

import android.animation.ObjectAnimator
import android.util.Log
import android.view.View

open class Loader {

    private val fadeAnimationDuration: Long = 300

    fun showLoader(progressBar: View, views: List<View>) {
        for (view in views) {
            view.isEnabled = false
            val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, .5f)
            animator.duration = fadeAnimationDuration
            animator.start()
        }

        progressBar.visibility = View.VISIBLE
        val animator = ObjectAnimator.ofFloat(progressBar, View.ALPHA, 1f)
        animator.duration = fadeAnimationDuration

        animator.start()
    }

    fun hideLoader(progressBar: View, views: List<View>) {
        for (view in views) {
            view.isEnabled = true
            val animator = ObjectAnimator.ofFloat(view, View.ALPHA, .5f, 1f)
            animator.duration = fadeAnimationDuration
            animator.start()
        }

        progressBar.visibility = View.VISIBLE
        val animator = ObjectAnimator.ofFloat(progressBar, View.ALPHA, 0f)
        animator.duration = fadeAnimationDuration
        animator.start()
    }
}