package com.example.transporargo.main_fragments.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.transporargo.R
import com.example.transporargo.databinding.FragmentRequestFormSelectPlaceBinding
import com.example.transporargo.databinding.FragmentSearchFormSelectPlaceBinding
import com.example.transporargo.main_fragments.MainActivity
import com.example.transporargo.main_fragments.base_classes.BaseSelectPlaceMapFragment
import com.example.transporargo.main_fragments.ui.request_form.RequestFormViewModel
import com.google.android.gms.maps.CameraUpdateFactory

class RequestFormSelectPlaceFragment : BaseSelectPlaceMapFragment() {

    private val viewModel: RequestFormViewModel by activityViewModels()
    private lateinit var binding: FragmentRequestFormSelectPlaceBinding


    override fun onMapReadyCallback() {
        val args = RequestFormSelectPlaceFragmentArgs.fromBundle(requireArguments())
        isPlaceFrom = args.isPlaceFrom

        if (isPlaceFrom) {
            binding.saveBtn.text = context?.getText(R.string.save_from_place)

            if (viewModel.placeFromLatLng.value != null) {
                addMarker(
                    parseLatLngString(viewModel.placeFromLatLng.value!!),
                    viewModel.placeFromName.value!!
                )
            }
        } else {
            binding.saveBtn.text = context?.getText(R.string.save_to_place)

            if (viewModel.placeToLatLng.value != null) {
                addMarker(
                    parseLatLngString(viewModel.placeToLatLng.value!!),
                    viewModel.placeToName.value!!
                )
            }
        }


        if (args.clientLatLng.isEmpty()) {
            return
        }

        val zoom = 18f
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                parseLatLngString(args.clientLatLng),
                zoom
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val actionBarTitle =
            context?.getText(R.string.select_point_of_interest)
        activity?.let { activity ->
            (activity as MainActivity).setActionBarTitle(actionBarTitle.toString())
        }

        binding = FragmentRequestFormSelectPlaceBinding.inflate(inflater)

        binding.saveBtn.setOnClickListener {
            if (placeName == null || latLngStr == null) {
                Toast.makeText(
                    context,
                    context?.getText(R.string.point_of_interest_not_selected),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (isPlaceFrom) {
                viewModel.placeFromName.value = placeName
                viewModel.placeFromLatLng.value = latLngStr
            } else {
                viewModel.placeToName.value = placeName
                viewModel.placeToLatLng.value = latLngStr
            }

            findNavController().navigate(
                RequestFormSelectPlaceFragmentDirections.actionSelectPlaceFragmentToAddRequestFragment()
            )
        }

        return binding.root
    }


}