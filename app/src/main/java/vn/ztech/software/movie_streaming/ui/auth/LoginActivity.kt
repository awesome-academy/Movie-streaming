package vn.ztech.software.movie_streaming.ui.auth

import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.R
import vn.ztech.software.movie_streaming.base.BaseActivity
import vn.ztech.software.movie_streaming.base.BaseViewModel
import vn.ztech.software.movie_streaming.databinding.ActivityLoginBinding
import vn.ztech.software.movie_streaming.ui.MainActivity
import vn.ztech.software.movie_streaming.utils.extensions.showAlertDialog

class LoginActivity() : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    override val viewModel: BaseViewModel by viewModel()
    private val fireBaseAuth = FirebaseAuth.getInstance()
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result: FirebaseAuthUIAuthenticationResult ->

        if (result.resultCode == RESULT_OK) {
            //success
            openMainActivity()
        } else {
            //failed
        }
    }
    private val listAuthProviders = listOf(
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.PhoneBuilder().build(),
    )

    override fun initialize() {
        binding?.buttonLogin?.setOnClickListener {
            startSignIn()
        }
        if (fireBaseAuth.currentUser != null) {
            openMainActivity()
        }
    }

    private fun startSignIn() {
        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(listAuthProviders)
            .setLogo(R.drawable.image_welcome)
            .setTheme(R.style.ThemeLoginUI)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun openMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        this.showAlertDialog(
            R.string.dialog_title_quit_app,
            R.string.dialog_message_quit_app,
            onClickOkListener = { _, _ -> finish() }
        )
    }
}
