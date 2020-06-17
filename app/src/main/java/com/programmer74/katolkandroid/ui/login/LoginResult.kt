package com.programmer74.katolkandroid.ui.login

import com.programmer74.katolk.FeignRepository

data class LoginResult(
  var error: Exception? = null,
  var success: FeignRepository? = null
)
