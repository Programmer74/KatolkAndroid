package com.programmer74.katolkandroid.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.programmer74.katolk.FeignRepository
import com.programmer74.katolkandroid.R

class LoginViewModel() : ViewModel() {

  private val _loginForm = MutableLiveData<LoginFormState>()
  val loginFormState: LiveData<LoginFormState> = _loginForm

  private val _loginResult = MutableLiveData<LoginResult>()
  val loginResult: LiveData<LoginResult> = _loginResult

  fun login(url: String, username: String, password: String) {
    Thread(Runnable {
      try {
        val feignRepo = FeignRepository(url)
        feignRepo.obtainTokenByUsernamePassword(username, password)
        _loginResult.postValue(LoginResult(success = feignRepo))
      } catch (e: Exception) {
        _loginResult.postValue(LoginResult(error = e))
      }
    }).start()
  }

  fun loginDataChanged(URL: String, username: String, password: String) {
    if (!isURLValid(URL)) {
      _loginForm.value = LoginFormState(usernameError = R.string.invalid_url)
    } else if (!isUserNameValid(username)) {
      _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
    } else if (!isPasswordValid(password)) {
      _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
    } else {
      _loginForm.value = LoginFormState(isDataValid = true)
    }
  }

  private fun isURLValid(username: String): Boolean {
    return username.isNotBlank()
  }

  private fun isUserNameValid(username: String): Boolean {
    return username.isNotBlank()
  }

  private fun isPasswordValid(password: String): Boolean {
    return password.isNotBlank()
  }
}