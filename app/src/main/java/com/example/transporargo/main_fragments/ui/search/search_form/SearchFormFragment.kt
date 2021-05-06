package com.example.transporargo.main_fragments.ui.search.search_form

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.transporargo.BuildConfig
import com.example.transporargo.R
import com.example.transporargo.databinding.FragmentSearchFormBinding
import com.example.transporargo.main_fragments.MainActivity
import com.example.transporargo.main_fragments.base_classes.BaseFormFragment
import com.example.transporargo.main_fragments.ui.search.SearchViewModel
import com.example.transporargo.utils.isNetworkAvailable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.net.UnknownHostException

class SearchFormFragment : BaseFormFragment() {

    private lateinit var binding: FragmentSearchFormBinding
    private val viewModel: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentSearchFormBinding.inflate(inflater)
        binding.viewModel = viewModel

        val actionBarTitle = context?.getString(R.string.search)
        activity?.let { activity ->
            (activity as MainActivity).setActionBarTitle(actionBarTitle.toString())
        }

        setIsCargoObserver()
        setToastTextObserver()
        getClientLocation()
        setPlacesObservers()
        viewsChanging()
        setSaveBtnClickListener()

        return binding.root
    }

    private fun setIsCargoObserver() {
        viewModel.isCargo.observe(viewLifecycleOwner, Observer { isCargo ->
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
        viewModel.toastText.observe(viewLifecycleOwner, Observer { toastString ->
            toastString?.let {
                Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setPlacesObservers() {
        binding.placeFrom.setOnClickListener {
            hideKeyboard(it)
            if (!foregroundAndBackgroundLocationPermissionApproved()) {
                showEnablingLocationSnackbar(binding.searchFormContainer)
                return@setOnClickListener
            }
            if (!isClientLatLngInitialized()) {
                viewModel.showLoader()
                getClientLocation {
                    viewModel.hideLoader()
                    navigateToSelectPlaceFragment(true)
                }
            } else {
                navigateToSelectPlaceFragment(true)
            }
        }

        binding.placeTo.setOnClickListener {
            hideKeyboard(it)
            if (!foregroundAndBackgroundLocationPermissionApproved()) {
                showEnablingLocationSnackbar(binding.searchFormContainer)
                return@setOnClickListener
            }

            if (!isClientLatLngInitialized()) {
                viewModel.showLoader()
                getClientLocation {
                    viewModel.hideLoader()
                    navigateToSelectPlaceFragment(false)
                }
            } else {
                navigateToSelectPlaceFragment(false)
            }
        }}

    private fun navigateToSelectPlaceFragment(isPlaceFrom: Boolean) {
        findNavController().navigate(
            SearchFormFragmentDirections.actionSearchFragmentToSearchFormSelectPlaceFragment(
                clientLatLngStr,
                isPlaceFrom
            )
        )
    }

    private fun setSaveBtnClickListener() {
        binding.searchRequestsBtn.setOnClickListener {
            it.isEnabled = false
            try {
                if (!isNetworkAvailable(requireActivity().application)) {
                    throw UnknownHostException()
                }
                viewModel.showLoader()
                if (viewModel.isFormFillingValid()) {
                    viewModel.hideLoader()
                    findNavController().navigate(SearchFormFragmentDirections.actionSearchFragmentToRequestListFragment())
                }
            } catch (e: UnknownHostException) {
                viewModel.setToastText(requireContext().getString(R.string.no_internet_connection))
            } finally {
                it.isEnabled = true
            }
        }
    }

    private fun viewsChanging() {
        if (viewModel.placeFromLatLng.value != null) {
            binding.placeFrom.setText(viewModel.placeFromName.value)
        }

        if (viewModel.placeToLatLng.value != null) {
            binding.placeTo.setText(viewModel.placeToName.value)
        }

        binding.placeFrom.isFocusable = false
        binding.placeTo.isFocusable = false
    }
}