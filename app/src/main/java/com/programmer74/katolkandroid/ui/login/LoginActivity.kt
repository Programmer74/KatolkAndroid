package com.programmer74.katolkandroid.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.programmer74.katolk.FeignRepository
import com.programmer74.katolk.KatolkModel
import com.programmer74.katolkandroid.ui.main.MainActivity
import com.programmer74.katolkandroid.R
import com.programmer74.katolkandroid.ui.KatolkModelSingleton
import java.util.function.Consumer

class LoginActivity : AppCompatActivity() {

  private lateinit var loginViewModel: LoginViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_login)

    val url = findViewById<EditText>(R.id.url)
    val username = findViewById<EditText>(R.id.username)
    val password = findViewById<EditText>(R.id.password)
    val login = findViewById<Button>(R.id.login)
    val loading = findViewById<ProgressBar>(R.id.loading)

    loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
        .get(LoginViewModel::class.java)

    loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
      val loginState = it ?: return@Observer

      // disable login button unless both username / password is valid
      login.isEnabled = loginState.isDataValid

      if (loginState.usernameError != null) {
        username.error = getString(loginState.usernameError)
      }
      if (loginState.passwordError != null) {
        password.error = getString(loginState.passwordError)
      }
    })

    loginViewModel.loginResult.observe(this@LoginActivity, Observer {
      val loginResult = it ?: return@Observer

      loading.visibility = View.GONE
      if (loginResult.error != null) {
        showLoginFailed(loginResult.error)
      }
      if (loginResult.success != null) {
        updateUiWithUser(loginResult.success)
      }
      setResult(Activity.RESULT_OK)

      //Complete and destroy login activity once successful
      finish()
    })

    url.afterTextChanged {
      loginViewModel.loginDataChanged(
          url.text.toString(),
          username.text.toString(),
          password.text.toString()
      )
    }

    username.afterTextChanged {
      loginViewModel.loginDataChanged(
          url.text.toString(),
          username.text.toString(),
          password.text.toString()
      )
    }

    password.apply {
      afterTextChanged {
        loginViewModel.loginDataChanged(
            url.text.toString(),
            username.text.toString(),
            password.text.toString()
        )
      }

      setOnEditorActionListener { _, actionId, _ ->
        when (actionId) {
          EditorInfo.IME_ACTION_DONE ->
            loginViewModel.login(
                url.text.toString(),
                username.text.toString(),
                password.text.toString()
            )
        }
        false
      }

      login.setOnClickListener {
        loading.visibility = View.VISIBLE
        loginViewModel.login(url.text.toString(), username.text.toString(), password.text.toString())
      }
    }
  }

  private fun updateUiWithUser(feignRepository: FeignRepository?) {
    if (feignRepository != null) {
      val katolkModel = KatolkModel(feignRepository)
      val v = findViewById<View>(android.R.id.content).rootView

      katolkModel.setup(Consumer {
        runOnUiThread {
          msgBox(v, "You are '${it.username}'")
          KatolkModelSingleton.setMe(it)
        }
        katolkModel.scheduleDialogueListUpdate()
      })

      KatolkModelSingleton.setKatolkModel(katolkModel)

      val activity = Intent(this, MainActivity::class.java)
      startActivity(activity)
    }
  }

  private fun showLoginFailed(error: Exception?) {
    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
  }

  private fun msgBox(v: View, text: String) {
    Snackbar.make(v, text, Snackbar.LENGTH_LONG).show()
  }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
  this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(editable: Editable?) {
      afterTextChanged.invoke(editable.toString())
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
  })
}