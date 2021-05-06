package com.example.transporargo.authentication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.transporargo.R
import com.example.transporargo.databinding.FragmentSignUpBinding
import com.example.transporargo.utils.Loader


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding

    private val fadeAnimationDuration: Long = 500
    private val TAG = "SignUpFragment"

    private val viewModel: AuthViewModel by activityViewModels()

    private var stepName = "email"

    private val loader = Loader()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        viewModel.toastText.observe(viewLifecycleOwner, Observer { toastText ->
            if (toastText.isNotEmpty()) {
                viewModel.setToastText("")
                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
            }
        })

        binding.viewModel = viewModel

        viewModel.showLoader.observe(viewLifecycleOwner, Observer { needToShowLoader ->
            val views = when (stepName) {
                "email" -> listOf(
                    binding.registrationImg,
                    binding.emailInput,
                    binding.emailAddingExplain,
                    binding.nextBtn
                )
                "nameNSurname" -> listOf(
                    binding.nameNSurnameAdding,
                    binding.nameInput,
                    binding.submitNameNSurname,
                    binding.surnameInput
                )
                "password" -> listOf(
                    binding.passwordAddingExplain,
                    binding.passwordInput,
                    binding.repeatPasswordInput,
                    binding.submitRegistration
                )
                else -> listOf()
            }

            if (views.isEmpty()) {
                return@Observer
            }

            if (needToShowLoader) {
                loader.showLoader(binding.progressBar, views)
            } else {
                loader.hideLoader(binding.progressBar, views)
            }
        })

        binding.nextBtn.setOnClickListener submitEmail@{ submitEmail ->

            viewModel.isEmailForRegistrationValid()

            viewModel.emailValidationErrorText.observe(viewLifecycleOwner, Observer { errorStr ->
                if (errorStr == "") {
                    submitNameNSurnameStep(submitEmail)
                    binding.submitNameNSurname.setOnClickListener submitNameNSurnameBtn@{ submitNameNSurnameBtn ->
                        if (!viewModel.isSurnameNNameValid()) {
                            return@submitNameNSurnameBtn
                        }

                        showPasswordStep(submitNameNSurnameBtn)
                        binding.submitRegistration.setOnClickListener submitRegistrationBtn@{ submitRegistrationBtn ->
                            if (!viewModel.isPasswordsEnterCorrectlyForRegistration()) {
                                return@submitRegistrationBtn
                            }

                            viewModel.register()
                            viewModel.isRegistrationSuccess.observe(
                                viewLifecycleOwner,
                                Observer { isSuccess ->
                                    if (isSuccess) {
                                        showSuccessRegistrationStep(submitRegistrationBtn)
                                    }
                                })
                        }
                    }
                } else {
                    viewModel.setToastText(errorStr)
                }
            })
        }

        viewModel.userInfo.observe(viewLifecycleOwner, Observer { userInfo ->
            userInfo?.let {
                viewModel.saveUser(it)
            }
        })

        return binding.root
    }

    private fun submitNameNSurnameStep(btn: View) {
        stepName = "nameNSurname"
        hideKeyboard(btn)
        btn.isEnabled = false

        for (view in listOf(btn, binding.emailAddingExplain, binding.emailInput)) {
            val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f)
            animator.duration = fadeAnimationDuration
            animator.start()
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.GONE
                }
            })
        }

        for (view in listOf(
            binding.nameNSurnameAdding, binding.nameInput,
            binding.surnameInput, binding.submitNameNSurname
        )) {
            val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f)
            animator.duration = fadeAnimationDuration

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    view.visibility = View.VISIBLE
                }
            })

            animator.start()
        }
    }

    private fun showPasswordStep(btn: View) {
        hideKeyboard(btn)
        for (view in listOf(
            binding.nameNSurnameAdding, binding.nameInput,
            binding.surnameInput, btn
        )) {
            stepName = "password"
            val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f)
            animator.duration = fadeAnimationDuration
            animator.start()
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.GONE
                }
            })
        }

        for (view in listOf(
            binding.passwordAddingExplain, binding.passwordInput,
            binding.repeatPasswordInput, binding.submitRegistration
        )) {
            val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f)
            animator.duration = fadeAnimationDuration

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    view.visibility = View.VISIBLE
                }
            })

            animator.start()
        }
    }

    private fun showSuccessRegistrationStep(btn: View) {
        hideKeyboard(btn)
        stepName = "success"
        btn.isEnabled = false

        for (view in listOf(
            binding.passwordAddingExplain, binding.passwordInput,
            binding.repeatPasswordInput, binding.submitRegistration, binding.registrationImg
        )) {
            val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f)
            animator.duration = fadeAnimationDuration

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.GONE
                }
            })

            animator.start()
        }

        for (view in listOf(
            binding.registrationSuccessText, binding.successRegistrationImg,
            binding.startUseAppBtn
        )) {
            val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f)
            animator.duration = fadeAnimationDuration

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    view.visibility = View.VISIBLE
                }
            })

            animator.start()
        }

        binding.startUseAppBtn.setOnClickListener {
            startUsingApp()
        }
    }

    private fun startUsingApp() {
        (activity as AuthenticationActivity).startRequestListActivity()
    }

    private fun hideKeyboard(view: View) {
        val imm: InputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}