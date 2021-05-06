package com.example.transporargo.main_fragments.base_classes

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.activityViewModels
import com.example.transporargo.BuildConfig
import com.example.transporargo.R
import com.example.transporargo.main_fragments.ui.request_form.RequestFormViewModel
import com.example.transporargo.utils.Loader
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

abstract class BaseFormFragment: Fragment() {

    private val runningQOrLater = android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.Q

    private val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

    protected val loader = Loader()

    private lateinit var flc: FusedLocationProviderClient

    protected lateinit var clientLatLngStr: String

    @TargetApi(29)
    protected fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==

                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                )
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )

            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    @TargetApi(29)
    protected fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val resultCode = when {
            runningQOrLater -> {
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }

        ActivityCompat.requestPermissions(
            requireActivity(),
            permissionsArray,
            resultCode
        )
    }

    protected fun showEnablingLocationSnackbar(container: View) {
        Snackbar.make(
            container,
            R.string.location_call_permission_denied_explanation,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.settings) {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
    }

    protected fun getClientLocation(callback: () -> Unit = {}) {
        flc = LocationServices.getFusedLocationProviderClient(requireActivity())
        requestForegroundAndBackgroundLocationPermissions()

        flc.lastLocation.addOnSuccessListener { location ->
            clientLatLngStr = if (location == null) {
                ""
            } else {
                "${location.latitude}|${location.longitude}"
            }
            callback()
        }
    }

    protected fun isClientLatLngInitialized(): Boolean {
        return ::clientLatLngStr.isInitialized
    }

    protected fun hideKeyboard(view: View) {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}