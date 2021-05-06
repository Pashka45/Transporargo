package com.example.transporargo.authentication

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import com.example.transporargo.R
import com.example.transporargo.main_fragments.data.local.LocalDataSource
import com.example.transporargo.main_fragments.data.local.dto.UsersInfoDTO
import com.example.transporargo.main_fragments.data.local.getDatabase
import com.example.transporargo.main_fragments.data.remote.RemoteDataSource
import com.example.transporargo.main_fragments.data.remote.dto.UsersInfoServerDTO
import com.example.transporargo.utils.Authentication
import com.example.transporargo.utils.FileHandler
import com.example.transporargo.utils.isNetworkAvailable
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class AuthViewModel(private val context: Application) : ViewModel() {

    private val remoteDataSource = RemoteDataSource(FileHandler(context))
    private val localDataSource = LocalDataSource(getDatabase(context))

    val regEmail = MutableLiveData<String>()
    val regPassword = MutableLiveData<String>()
    val regRepeatedPassword = MutableLiveData<String>()

    val regName = MutableLiveData<String>()
    val regSurname = MutableLiveData<String>()

    val loginEmail = MutableLiveData<String>()
    val loginPassword = MutableLiveData<String>()


    private val _showLoader = MutableLiveData<Boolean>()
    val showLoader: LiveData<Boolean>
        get() = _showLoader

    private val _userInfo = MutableLiveData<UsersInfoDTO>()
    val userInfo: LiveData<UsersInfoDTO>
        get() = _userInfo

    private val _isRegistrationSuccess = MutableLiveData<Boolean>()
    val isRegistrationSuccess: LiveData<Boolean>
        get() = _isRegistrationSuccess

    private val _needToStartRequestsActivity = MutableLiveData<Boolean>()
    val needToStartRequestsActivity: LiveData<Boolean>
        get() = _needToStartRequestsActivity

    private val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String>
        get() = _toastText

    private val _emailValidationErrorText = MutableLiveData<String>()
    val emailValidationErrorText: LiveData<String>
        get() = _emailValidationErrorText


    private val TAG = "AuthViewModel"

    fun isEmailForRegistrationValid() {

        viewModelScope.launch {
            if (isEmailValid(regEmail.value)) {

                _showLoader.postValue(true)
                try {
                    if (!isNetworkAvailable(context)) {
                        throw UnknownHostException()
                    }
                    remoteDataSource.validateEmail(regEmail.value!!) { errorStrId ->
                        _emailValidationErrorText.postValue(
                            if (errorStrId == 0)
                                ""
                            else
                                context.getString(errorStrId)
                        )
                        _showLoader.postValue(false)
                    }
                } catch (e: UnknownHostException) {
                    _showLoader.postValue(false)
                    setToastText(context.getString(R.string.no_internet_connection))
                }

            }
        }
    }

    private fun isEmailForLoginValid(): Boolean {
        return isEmailValid(loginEmail.value)
    }

    private fun isEmailValid(email: String?): Boolean {
        if (!email.isNullOrEmpty()) {
            return when {
                Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    true
                }
                !email.contains("@") -> {
                    _emailValidationErrorText.value =
                        context.getString(R.string.email_must_include_sign)
                    false
                }
                else -> {
                    _emailValidationErrorText.value = context.getString(R.string.email_is_invalid)
                    false
                }
            }
        }

        _toastText.value = context.getString(R.string.email_is_empty)
        return false
    }

    private fun isLoginDataValid(): Boolean {
        return isEmailForLoginValid() && isPasswordsEnterCorrectlyForLogin()
    }

    fun isPasswordsEnterCorrectlyForRegistration(): Boolean {
        return when {
            regPassword.value.isNullOrEmpty() -> {
                _toastText.value = context.getString(R.string.password_field_is_empty)
                false
            }
            regRepeatedPassword.value.isNullOrEmpty() -> {
                _toastText.value = context.getString(R.string.repeat_password_field_is_empty)
                false
            }
            (regPassword.value != regRepeatedPassword.value) -> {
                _toastText.value =
                    context.getString(R.string.repeated_password_not_equal_to_password)
                false
            }
            (regPassword.value == regRepeatedPassword.value) -> {
                _toastText.value = ""
                true
            }
            else -> false
        }
    }

    private fun isPasswordsEnterCorrectlyForLogin(): Boolean {
        if (loginPassword.value.isNullOrEmpty()) {
            _toastText.value = context.getString(R.string.password_field_is_empty)
            return false
        }

        return true
    }

    fun login() {
        if (isLoginDataValid()) {
            _showLoader.value = true
            viewModelScope.launch {
                try {
                    if (!isNetworkAvailable(context)) {
                        throw UnknownHostException()
                    }
                    remoteDataSource.login(
                        loginEmail.value!!,
                        loginPassword.value!!
                    ) { userInfoRes ->
                        if (userInfoRes == null) {
                            _toastText.postValue(context.getString(R.string.wrong_email_or_password))
                        } else {
                            Authentication.setAuthInfo(userInfoRes)
                            _userInfo.postValue(userInfoRes)
                        }
                        _showLoader.postValue(false)

                    }
                } catch (e: UnknownHostException) {
                    _showLoader.postValue(false)
                    _toastText.postValue(context.getString(R.string.no_internet_connection))
                }
            }
        }
    }

    fun saveUser(userInfo: UsersInfoDTO) {
        viewModelScope.launch {
            Log.i("userInfo", userInfo.email)
            localDataSource.saveUserInfo(userInfo)
            startRequestsActivity()
        }
    }

    fun register() {
        _showLoader.value = true
        viewModelScope.launch {
            try {
                if (!isNetworkAvailable(context)) {
                    throw UnknownHostException()
                }

                remoteDataSource.addUser(
                    UsersInfoServerDTO(
                        regEmail.value!!,
                        regPassword.value!!,
                        regName.value!!,
                        regSurname.value!!
                    )
                ) { userInfoRes ->
                    if (userInfoRes == null) {
                        _showLoader.postValue(false)
                        _toastText.postValue(context.getString(R.string.smth_went_wrong_try_later))
                    } else {
                        Log.i("userInfoRes", userInfoRes.toString())
                        Authentication.setAuthInfo(userInfoRes)
                        _userInfo.postValue(userInfoRes)
                        _showLoader.postValue(false)
                        _isRegistrationSuccess.postValue(true)
                    }
                }
            } catch (e: UnknownHostException) {
                _showLoader.postValue(false)
                _toastText.postValue(context.getString(R.string.no_internet_connection))
            }
        }
    }

    private fun startRequestsActivity() {
        _needToStartRequestsActivity.postValue(true)
    }

    fun isSurnameNNameValid(): Boolean {
        return when {
            regName.value.isNullOrEmpty() -> {
                _toastText.value = context.getString(R.string.name_field_is_empty)
                return false
            }
            regSurname.value.isNullOrEmpty() -> {
                _toastText.value = context.getString(R.string.surname_field_is_empty)
                return false
            }
            else -> true
        }
    }

    fun setToastText(text: String) {
        _toastText.value = text
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}