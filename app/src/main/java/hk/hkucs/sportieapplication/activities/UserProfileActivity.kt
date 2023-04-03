package hk.hkucs.sportieapplication.activities

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.databinding.ActivityUserProfileBinding
import hk.hkucs.sportieapplication.firestore.FirestoreClass
import hk.hkucs.sportieapplication.models.User
import hk.hkucs.sportieapplication.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import hk.hkucs.sportieapplication.utils.SGlideLoader
import java.io.IOException

class UserProfileActivity : AppCompatActivity() {
    private lateinit var mUserDetails: User
    private lateinit var binding: ActivityUserProfileBinding

    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
            showUserProfilePage(mUserDetails)
        } else {
            FirestoreClass().getUserDetails(this)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.profile

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.booking -> {
                    startActivity(Intent(applicationContext, BookingListActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.groups -> {
                    startActivity(Intent(applicationContext, PlayerCountActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.home -> {
                    startActivity(Intent(applicationContext, BookingActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.friends -> {
                    startActivity(Intent(applicationContext, BookmarkRecomActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> return@setOnItemSelectedListener true
            }
            false
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String, Any>()
        val mobileNumber = binding.etMobileNumber.text.toString().trim{ it <= ' ' }

        val gender = if (binding.rbMale.isChecked) { Constants.MALE } else { Constants.FEMALE }
        userHashMap[Constants.GENDER] = gender

        if (mobileNumber.isNotEmpty()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }
        else{
            Toast.makeText(
                this, "Insert mobile number",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }
        else{
            Toast.makeText(
                this, "Insert picture?",
                Toast.LENGTH_SHORT
            ).show()
        }

        userHashMap[Constants.COMPLETE_PROFILE] = 1

        FirestoreClass().updateUserDetails(this, userHashMap)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            }
        } else {
            Toast.makeText(
                this, "Denied the permission for storage. Allow from settings",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        SGlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivUserPhoto)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            "Image selection Failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    // check if the entry of mobile number is empty
    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' '}) -> {
                Toast.makeText(
                    this@UserProfileActivity,
                    "Please enter mobile number!",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }

    // function for successfully updating user profile
    fun userProfileUpdateSuccess(){
        Toast.makeText(
            this@UserProfileActivity,
            "Your profile data was updated successfully",
            Toast.LENGTH_SHORT
        ).show()

        finish()
        startActivity((Intent(this@UserProfileActivity, UserProfileActivity::class.java)))
    }

    // function for successfully uploading image
    fun imageUploadSuccess(imageURL: String){
        Toast.makeText(
            this@UserProfileActivity,
            "Your image is uploaded successfully. Image URL is $imageURL",
            Toast.LENGTH_SHORT
        ).show()
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    fun showUserProfilePage(user: User) {
        mUserDetails = user
        // prevent user to edit the fields anymore in profile screen
        binding.etFirstName.isEnabled = false
        binding.etFirstName.setText(mUserDetails.firstName)

        binding.etLastName.isEnabled = false
        binding.etLastName.setText(mUserDetails.lastName)

        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mUserDetails.email)

        if (mUserDetails.profileCompleted == 1) {
            if (mUserDetails.gender == "male") {
                binding.rbMale.isChecked
                binding.rbMale.isEnabled = false
            } else {
                binding.rbFemale.isChecked
                binding.rbFemale.isEnabled = false
            }
            binding.etMobileNumber.setText(mUserDetails.mobile.toString())
            binding.etMobileNumber.isEnabled = false
            binding.btnSubmit.visibility = View.GONE
            binding.rgGender.isClickable = false
        }

        // The uri of selected image from phone storage.
        mSelectedImageFileUri = mUserDetails.image.toUri()
        // set the imageview photo to the image from the data (link of location of the image)
        SGlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivUserPhoto)

        binding.ivUserPhoto.setOnClickListener() {
            if (it != null) {
                if(binding.btnSubmit.visibility == View.GONE){
                    binding.btnSubmit.visibility = View.VISIBLE
                }
                when (it.id) {
                    R.id.iv_user_photo -> {
                        // Here we will check if the permission is already allowed or we need to request for it.
                        // First of all we will check the READ EXTERNAL STORAGE permission and if it is hot allowed we
                        if (ContextCompat.checkSelfPermission(
                                this,
                                READ_EXTERNAL_STORAGE
                            )
                            == PackageManager.PERMISSION_GRANTED
                        ) {
//                            Toast.makeText(this, "You already have the storage permission.", Toast.LENGTH_LONG).show()
                            Constants.showImageChooser(this)
                        } else {
                            /*Requests permissions to be granted to this application. These permissions must be requested in your manifest,
                            they should not be granted to your app, and they should have protection level*/
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(READ_EXTERNAL_STORAGE),
                                Constants.READ_STORAGE_PERMISSION_CODE
                            )
                        }
                    }
                }
            }
        }

        binding.btnSubmit.setOnClickListener() {
            if (validateUserProfileDetails()) {
                // upload image to cloud storage, BEFORE THE HASHMAP, IF ONLY THE URI IS NOT EMPTY
                if (mSelectedImageFileUri != null) {
                    FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)
                }
                else {
                    updateUserProfileDetails()
                }
            }
        }


    }
}