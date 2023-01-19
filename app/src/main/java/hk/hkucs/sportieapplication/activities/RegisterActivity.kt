package hk.hkucs.sportieapplication.activities


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import hk.hkucs.sportieapplication.firestore.FirestoreClass
import hk.hkucs.sportieapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hk.hkucs.sportieapplication.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize binding class with contentView as root
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // hide top bar
        @Suppress ("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        binding.signUpLogInText.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.signUpButton.setOnClickListener {
            val firstName = binding.firstNameEt.text.toString().trim{it <= ' '}
            val lastName = binding.lastNameEt.text.toString().trim{it <= ' '}
            val email = binding.emailEt.text.toString().trim{it <= ' '}
            val pass = binding.passEt.text.toString().trim{it <= ' '}
            val confirmPass = binding.confirmPassEt.text.toString().trim{it <= ' '}

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val firebaseUser: FirebaseUser = it.result!!.user!!
                                val user = User(firebaseUser.uid, firstName, lastName, email)
                                FirestoreClass().registerUser(this, user)
//                                firebaseAuth.signOut()
                                finish()
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun userRegistrationSuccess() {
        Toast.makeText(this, "Account successfully registered", Toast.LENGTH_SHORT).show()
    }
}
