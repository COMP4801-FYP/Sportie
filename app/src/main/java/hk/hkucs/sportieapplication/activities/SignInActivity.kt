package hk.hkucs.sportieapplication.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import hk.hkucs.sportieapplication.databinding.ActivitySignInBinding
import hk.hkucs.sportieapplication.firestore.FirestoreClass
import hk.hkucs.sportieapplication.models.User
import hk.hkucs.sportieapplication.utils.Constants

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize binding class with contentView as root
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // hide top bar
        @Suppress ("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        binding.signInRegisterText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirestoreClass().getUserDetails(this@SignInActivity)
                            val intent = Intent(this, UserProfileActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun userLoggedInSuccess(user: User){
        if (user.profileCompleted == 0) {
            // If the user profile is incomplete then launch the UserProfileActivity.
            val intent = Intent(this@SignInActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        }
        else {
            // Redirect the user to Main Screen after log in.
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        }
        finish()
    }
}
