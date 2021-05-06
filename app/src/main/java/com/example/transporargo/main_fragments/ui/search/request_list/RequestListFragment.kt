package com.example.transporargo.main_fragments.ui.search.request_list

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.transporargo.BuildConfig
import com.example.transporargo.R
import com.example.transporargo.databinding.FragmentRequestListBinding
import com.example.transporargo.main_fragments.MainActivity
import com.example.transporargo.main_fragments.ui.Request
import com.example.transporargo.main_fragments.ui.myrequests.recyclerview.RequestListAdapter
import com.example.transporargo.main_fragments.ui.myrequests.recyclerview.RequestListsListener
import com.example.transporargo.main_fragments.ui.search.SearchViewModel
import com.example.transporargo.utils.Loader
import com.google.android.material.snackbar.Snackbar

class RequestListFragment : Fragment() {

    private lateinit var binding: FragmentRequestListBinding
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var adapter: RequestListAdapter
    private val loader = Loader()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requestPhoneCallPermission()
        binding = FragmentRequestListBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.requestsRecycler.visibility = View.GONE

        binding.emptyRequestListExplain.visibility = View.GONE
        binding.emptyRequestListImg.visibility = View.GONE

        val actionBarTitle = context?.getString(R.string.requests_list)
        activity?.let { activity ->
            (activity as MainActivity).setActionBarTitle(actionBarTitle.toString())
        }

        adapter =
            RequestListAdapter(
                RequestListsListener({ fromPlaceLatLng, fromPlaceName, toPlaceLatLng, toPlaceName ->
                    findNavController().navigate(
                        RequestListFragmentDirections.actionRequestListFragmentToMapsFragment(
                            fromPlaceLatLng,
                            toPlaceLatLng,
                            fromPlaceName,
                            toPlaceName
                        )
                    )
                }, { phone ->
                    makeCallOrShowSnackbar(phone)
                })
            )
        binding.viewModel = viewModel

        binding.requestsRecycler.adapter = adapter

        adapter.isStart = true
        viewModel.getRequestListRemote()
        setHasOptionsMenu(true)

        viewModel.needToShowLoading.observe(viewLifecycleOwner, Observer { needToShowLoader ->

            if (needToShowLoader) {
                loader.showLoader(binding.progressBar, listOf())
            } else {
                loader.hideLoader(binding.progressBar, listOf())
            }
        })

        viewModel.isEmptyRequestList.observe(viewLifecycleOwner, Observer { isEmpty ->
            Log.i("isEmptyRequestList", "called $isEmpty")
            if (isEmpty) {
                binding.emptyRequestListImg.visibility = View.VISIBLE
                binding.emptyRequestListExplain.visibility = View.VISIBLE
            } else {
                binding.emptyRequestListImg.visibility = View.GONE
                binding.emptyRequestListExplain.visibility = View.GONE
            }
        })

        viewModel.showRecyclerView.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.requestsRecycler.visibility = View.VISIBLE
            } else {
                binding.requestsRecycler.visibility = View.GONE
            }
        })

        viewModel.foundRequests.observe(viewLifecycleOwner, Observer {
            viewModel.saveRequestList()
        })

        viewModel.requestsToShow.observe(viewLifecycleOwner, Observer {
            viewModel.addRequestsToAdapter(adapter)
        })
        setToastTextObserver()
        return binding.root
    }

    private fun setToastTextObserver() {
        viewModel.toastText.observe(viewLifecycleOwner, Observer { toastString ->
            toastString?.let {
                Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private val MY_PERMISSIONS_REQUEST_CALL_PHONE = 123


    private fun makeCallOrShowSnackbar(phone: String) {
        if (isPhoneCallPermissionGranted()) {
            val phoneCleared = phone.replace("[()\\s-]".toRegex(), "")
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneCleared")
            startActivity(callIntent)
        } else {
            Snackbar.make(
                binding.requestsContainer,
                R.string.phone_call_permission_denied_explanation,
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
    }

    private fun requestPhoneCallPermission() {
        if (!isPhoneCallPermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CALL_PHONE),
                MY_PERMISSIONS_REQUEST_CALL_PHONE
            )
        }
    }

    private fun isPhoneCallPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        viewModel.clear(adapter)
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        binding.emptyRequestListImg.visibility = View.GONE
    }
}