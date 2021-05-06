package com.example.transporargo.main_fragments.ui.myrequests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.transporargo.R
import com.example.transporargo.databinding.FragmentMyRequestsBinding
import com.example.transporargo.main_fragments.MainActivity
import com.example.transporargo.main_fragments.ui.myrequests.recyclerview.MyRequestsAdapter
import com.example.transporargo.main_fragments.ui.myrequests.recyclerview.MyRequestsListener
import com.example.transporargo.main_fragments.ui.request_form.RequestFormViewModel
import com.example.transporargo.utils.Loader
import com.example.transporargo.utils.isNetworkAvailable
import java.net.BindException
import java.net.UnknownHostException

class MyRequestsFragment : Fragment() {

    private lateinit var binding: FragmentMyRequestsBinding
    private lateinit var viewModel: MyRequestsViewModel
    private lateinit var adapter: MyRequestsAdapter
    private val loader = Loader()

    private val requestFormViewModel: RequestFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val actionBarTitle = context?.getString(R.string.my_requests)
        activity?.let { activity ->
            (activity as MainActivity).setActionBarTitle(actionBarTitle.toString())
        }

        binding = FragmentMyRequestsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        viewModel =
            ViewModelProvider(this, MyRequestsViewModel.Factory(requireActivity().application)).get(
                MyRequestsViewModel::class.java
            )

        adapter =
            MyRequestsAdapter(
                MyRequestsListener({ fromPlaceLatLngStr, fromPlaceName, toPlaceLatLngStr, toPlaceName ->
                    findNavController().navigate(
                        MyRequestsFragmentDirections.actionMyRequestsFragmentToMapsFragment(
                            fromPlaceLatLngStr,
                            toPlaceLatLngStr,
                            fromPlaceName,
                            toPlaceName
                        )
                    )
                }, { requestId ->

                    try {
                        if (!isNetworkAvailable(requireActivity().application)) {
                            throw UnknownHostException()
                        }

                        viewModel.deleteRequestRemote(requestId)

                        viewModel.deletedRequestId.observe(
                            viewLifecycleOwner,
                            Observer { requestDeletedId ->
                                if (!requestDeletedId.isNullOrEmpty()) {
                                    viewModel.clearDeletedRequestId()
                                    viewModel.deleteRequestLocal(requestId) {
                                        viewModel.myRequestList.value = viewModel.myRequestList.value?.filter { request ->
                                            request.id != requestId
                                        }

                                        viewModel.refreshRequestsListToAdapter(adapter)
                                    }
                                }
                            })
                    } catch (e: UnknownHostException) {
                        viewModel.setToastText(requireContext().getString(R.string.no_internet_connection))
                    }

                }, { requestId ->
                    try {
                        if (!isNetworkAvailable(requireActivity().application)) {
                            throw UnknownHostException()
                        }

                        requestFormViewModel.fillRequestFields(viewModel.getRequestById(requestId))
                        requestFormViewModel.manipulationType =
                            RequestFormViewModel.RequestManipulations.EDIT
                        findNavController().navigate(MyRequestsFragmentDirections.actionMyRequestsFragmentToAddRequestFragment())

                    } catch (e: UnknownHostException) {
                        viewModel.setToastText(requireContext().getString(R.string.no_internet_connection))
                    }

                }, { requestId ->
                    try {
                        if (!isNetworkAvailable(requireActivity().application)) {
                            throw UnknownHostException()
                        }
                        requestFormViewModel.fillRequestFields(viewModel.getRequestById(requestId), true)
                        requestFormViewModel.manipulationType =
                            RequestFormViewModel.RequestManipulations.ADD
                        findNavController().navigate(MyRequestsFragmentDirections.actionMyRequestsFragmentToAddRequestFragment())
                    } catch (e: UnknownHostException) {
                        viewModel.setToastText(requireContext().getString(R.string.no_internet_connection))
                    }
                })
            )
        binding.viewModel = viewModel

        binding.requestsRecycler.adapter = adapter

        viewModel.showRequestList()

        viewModel.myRequestList.observe(viewLifecycleOwner, Observer { requestList ->
            viewModel.addRequestsListToAdapter(adapter, requestList)
        })

        viewModel.requestsToSave.observe(viewLifecycleOwner, Observer { requestList ->
            if (!requestList.isNullOrEmpty()) {
                viewModel.saveRequests()
            }
        })

        viewModel.toastText.observe(viewLifecycleOwner, Observer { toastString ->
            if (!toastString.isNullOrEmpty()) {
                viewModel.setToastText("")
                Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show()
            }
        })

        setHasOptionsMenu(true)

        viewModel.needToShowLoading.observe(viewLifecycleOwner, Observer { needToShowLoader ->
            val views = listOf(binding.requestsRecycler)

            if (needToShowLoader) {
                loader.showLoader(binding.progressBar, views)
            } else {
                loader.hideLoader(binding.progressBar, views)
            }
        })

        viewModel.isEmptyRequestList.observe(viewLifecycleOwner, Observer {
            binding.emptyRequestListImg.visibility = View.VISIBLE
            binding.emptyRequestListExplain.visibility = View.VISIBLE
        })

        return binding.root
    }
}