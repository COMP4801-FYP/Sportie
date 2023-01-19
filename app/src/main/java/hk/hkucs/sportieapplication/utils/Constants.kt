package hk.hkucs.sportieapplication.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

// global name for database collection
object Constants {
    const val USERS: String = "user"

    // using sharedpreference to store limited user data (simple data type) in the device
    // shared preference name
    const val SNAP_PREFERENCES: String = "SnapSplitPrefs"
    // key for shared preference
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val LOGGED_IN_EMAIL: String = "logged_in_email"
    const val USER_IMAGE_PROFILE: String = "user_image_profile"

    const val EXTRA_USER_DETAILS: String = "extra_user_details"

    const val READ_STORAGE_PERMISSION_CODE = 2

    const val PICK_IMAGE_REQUEST_CODE = 1

    // To maintain the correctness of firebase field name and hashmap key
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"

    // for name of file in cloud storage
    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"


    // image chooser for reusability in different activities
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }


    // take care of getting the image file extension: jpg, png etc
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
        * MimeTypMap: Two-way map that maps MIME-types to file extensions and vice versa.
        * getSingleton(): Get the singleton instance of MimeTypeMap.
        * getExtensionFromMimeType: Return the registered extension for the given MIME type.
        * contentResolver. getType: Return the MIME type of the given content URL.
        */
        return MimeTypeMap.getSingleton ()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}