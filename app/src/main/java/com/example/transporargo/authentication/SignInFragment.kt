package com.example.transporargo.authentication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.transporargo.R
import com.example.transporargo.databinding.FragmentSignInBinding
import com.example.transporargo.utils.Loader
import com.example.transporargo.utils.isNetworkAvailable
import java.net.UnknownHostException

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    private val viewModel: AuthViewModel by activityViewModels()

    private val loader = Loader()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.signUpText.setOnClickListener {
            try {
                if (!isNetworkAvailable(requireActivity().application)) {
                    throw UnknownHostException()
                }
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())

            } catch (e: UnknownHostException) {
                Log.i("exception", "thrown")
                viewModel.setToastText(requireContext().getString(R.string.no_internet_connection))
            }
        }

        viewModel.showLoader.observe(viewLifecycleOwner, Observer { needToShowLoader ->
            val views = listOf(/*binding.appName,*/ binding.password, binding.email, binding.signInBtn,
                binding.registrationText, binding.signUpText)

            if (needToShowLoader) {
                loader.showLoader(binding.progressBar, views)
            } else {
                loader.hideLoader(binding.progressBar, views)
            }
        })

        viewModel.toastText.observe(viewLifecycleOwner, Observer { toastText ->
            if (toastText.isNotEmpty()) {
                viewModel.setToastText("")
                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.needToStartRequestsActivity.observe(viewLifecycleOwner, Observer {
            if (it) {
                startRequestActivity()
            }
        })

        binding.signInBtn.setOnClickListener {
            viewModel.login()
        }

        viewModel.userInfo.observe(viewLifecycleOwner, Observer { userInfo ->
            userInfo?.let {
                viewModel.saveUser(it)
            }
        })

        return binding.root
    }

    private fun startRequestActivity () {
        (activity as AuthenticationActivity).startRequestListActivity()
    }
}