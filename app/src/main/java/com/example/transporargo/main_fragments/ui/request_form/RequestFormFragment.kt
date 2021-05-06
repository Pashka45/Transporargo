package com.example.transporargo.main_fragments.ui.request_form

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.transporargo.BuildConfig
import com.example.transporargo.R
import com.example.transporargo.databinding.FragmentRequestFormBinding
import com.example.transporargo.main_fragments.MainActivity
import com.example.transporargo.main_fragments.base_classes.BaseFormFragment
import com.example.transporargo.utils.Loader
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class RequestFormFragment : BaseFormFragment() {

    private lateinit var binding: FragmentRequestFormBinding
    private val formViewModel: RequestFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRequestFormBinding.inflate(inflater)

        binding.viewModel = formViewModel

        val actionBarTitle = context?.getString(R.string.request_form)
        activity?.let { activity ->
            (activity as MainActivity).setActionBarTitle(actionBarTitle.toString())
        }

        setIsCargoObserver()
        setToastTextObserver()
        getClientLocation()
        setPlacesObservers()
        viewsChanging()
        setSaveBtnClickListener()
        setShowLoaderObserver()

        return binding.root
    }

    private fun setShowLoaderObserver() {
        formViewModel.showLoader.observe(viewLifecycleOwner, Observer { needToShow ->
            val views = listOf(
                binding.requestTypeRadioGroup,
                binding.addRequestBtn,
                binding.arrowForwarder,
                binding.capacityInput,
                binding.weightInput,
                binding.evaluateDateInput,
                binding.truckTypeInput,
                binding.cargoTypeInput,
                binding.placeFrom,
                binding.placeTo,
                binding.phoneInput
            )

            if (needToShow) {
                loader.showLoader(binding.progressBar, views)
            } else {
                loader.hideLoader(binding.progressBar, views)
            }
        })
    }

    private fun setIsCargoObserver() {
        formViewModel.isCargo.observe(viewLifecycleOwner, Observer { isCargo ->
            if (isCargo) {
                binding.truckTypeInput.visibility = View.GONE
                binding.cargoTypeInput.visibility = View.VISIBLE
            } else {
                binding.truckTypeInput.visibility = View.VISIBLE
                binding.cargoTypeInput.visibility = View.GONE
            }
        })
    }

    private fun setToastTextObserver() {
        formViewModel.toastText.observe(viewLifecycleOwner, Observer { toastString ->
            toastString?.let {
                Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show()
                binding.addRequestBtn.isEnabled = true
            }
        })
    }

    private fun setPlacesObservers() {
        binding.placeFrom.setOnClickListener {
            hideKeyboard(it)
            if (!foregroundAndBackgroundLocationPermissionApproved()) {
                showEnablingLocationSnackbar(binding.addRequestContainer)
                return@setOnClickListener
            }

            if (!isClientLatLngInitialized()) {
                formViewModel.showLoader()
                getClientLocation {
                    formViewModel.hideLoader()
                    navigateToSelectPlaceFragment(true)
                }
            } else {
                navigateToSelectPlaceFragment(true)
            }

        }

        binding.placeTo.setOnClickListener {
            hideKeyboard(it)
            if (!foregroundAndBackgroundLocationPermissionApproved()) {
                showEnablingLocationSnackbar(binding.addRequestContainer)
                return@setOnClickListener
            }

            if (!isClientLatLngInitialized()) {
                formViewModel.showLoader()
                getClientLocation {
                    formViewModel.hideLoader()
                    navigateToSelectPlaceFragment(false)
                }
            } else {
                navigateToSelectPlaceFragment(false)
            }
        }
    }

    private fun navigateToSelectPlaceFragment(isPlaceFrom: Boolean) {
        findNavController().navigate(
            RequestFormFragmentDirections.actionAddRequestFragmentToSelectPlaceFragment(
                clientLatLngStr,
                isPlaceFrom
            )
        )
    }

    private fun setSaveBtnClickListener() {
        binding.addRequestBtn.setOnClickListener {
            it.isEnabled = false
            if (formViewModel.validateRequest()) {
                formViewModel.manipulateWithRequest()

                formViewModel.isRequestAdded.observe(
                    viewLifecycleOwner,
                    Observer { isRequestAdded ->
                        if (isRequestAdded) {
                            findNavController().navigate(RequestFormFragmentDirections.actionAddRequestFragmentToMyRequestsFragment())
                        }
                        it.isEnabled = true
                    })
            }
        }
    }

    private fun viewsChanging() {
        formViewModel.isCargo.value?.let { isCargo ->
            if (isCargo) {
                binding.requestTypeRadioGroup.check(R.id.cargo_radio_btn)
            } else {
                binding.requestTypeRadioGroup.check(R.id.truck_radio_btn)
            }
        }

        if (formViewModel.placeFromLatLng.value != null) {
            binding.placeFrom.setText(formViewModel.placeFromName.value)
        }

        if (formViewModel.placeToLatLng.value != null) {
            binding.placeTo.setText(formViewModel.placeToName.value)
        }

        binding.placeFrom.isFocusable = false
        binding.placeTo.isFocusable = false
    }
}