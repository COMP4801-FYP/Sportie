package hk.hkucs.sportieapplication.firestore

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.activities.*
import hk.hkucs.sportieapplication.models.BookingInformation
import hk.hkucs.sportieapplication.models.SportCentre
import hk.hkucs.sportieapplication.models.User
import hk.hkucs.sportieapplication.utils.Constants
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mStorage = FirebaseStorage.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        // The "users" is collection name. If the collection is already created then it will not create AGAIN
        mFirestore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(userInfo.userid)
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge later, INSTEAD OF REPLACING THE FIELDS
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                //here call a function for transferring the result
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                Log.e(
                    activity.javaClass.simpleName, "Error while registering the user.", e
                )
            }
    }


    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    // get method
    // the activity can be different, so use Activity
    fun getUserDetails(activity: Activity) {
        // collection name to get the data
        mFirestore.collection(Constants.USERS)
            // The document id to get the Fields of user, get all of fields in the document
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                // create sharedPreferences called SNAP_PREFERENCES, with mode_private to ensure the data only available in the app
                val sharedPreferences =
                    activity.getSharedPreferences(
                        Constants.SNAP_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                // editor to put in string inside the sharedPreferences, in key-value pair
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // key: LOGGED_IN_USERNAME  - value: firstname lastname
                editor.putString(
                    Constants. LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.putString(
                    Constants.LOGGED_IN_EMAIL,
                    user.email
                )
                editor.apply()

                Common.currentUser = user

                // START Pass the result to the Login Activity.
                when (activity) {
                    is SignInActivity -> {
                        // Call a function of base activity for transferring the result to it.
                        activity.userLoggedInSuccess(user)
                    }

                    is UserProfileActivity -> {
                        activity.showUserProfilePage(user)
                    }
                }
            }.addOnFailureListener {
                Log.i("ccd  ", Constants.USERS)
            }
    }

    fun updateUserDetails(activity: Activity, userHashMap: HashMap<String,Any>) {
        mFirestore.collection(("user"))
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating the user details", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(activity, imageFileURI)
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapShot ->
            Log.e("Firebase Image URL", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapShot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())

                    val sharedPreferences =
                        activity.getSharedPreferences(
                            Constants.SNAP_PREFERENCES,
                            Context.MODE_PRIVATE
                        )
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(
                        Constants.USER_IMAGE_PROFILE,
                        uri.toString()
                    )
                    editor.apply()

                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
//                        is GroupDetailsANDNewGroupActivity -> {
//                            activity.imageUploadSuccess(uri.toString())
//                        }
                    }
                }
        }
            .addOnFailureListener { exception ->
                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun dateComparison(chosendate: String, curdate:String):Boolean{
//        println(chosendate.slice(6..9))
//        println(chosendate.slice(3..4))
//        println(chosendate.slice(0..1))
        // check year
        if (chosendate.slice(6..9).toInt() > curdate.slice(6..9).toInt() ){
            // month
            if (chosendate.slice(3..4).toInt() > curdate.slice(3..4).toInt() ) {
                return true
            }
            else if (chosendate.slice(3..4).toInt() == curdate.slice(3..4).toInt()){
                // day
                if (chosendate.slice(0..1).toInt() >= curdate.slice(0..1).toInt() ){
                    return true
                }
            }
        }
        else if (chosendate.slice(6..9).toInt() == curdate.slice(6..9).toInt()){
            // month
            if (chosendate.slice(3..4).toInt() > curdate.slice(3..4).toInt() ) {
                return true
            }
            else{
                // day
                if (chosendate.slice(0..1).toInt() >= curdate.slice(0..1).toInt() ){
                    return true
                }
            }
        }
        return false
    }

    fun getBookingList(bookingListActivity: BookingListActivity, whentime: String) {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .collection("Booking")
            .get()
            .addOnSuccessListener { booking_doc ->
                if (!booking_doc.isEmpty) {
                    var bookings = ArrayList<BookingInformation>()
                    for (document in booking_doc) {
                        println(document.data)
                        var b = document.data

                        var t = 1438910477
                        var ts = Timestamp(t.toLong(), 0)

                        val formatter = SimpleDateFormat("dd/MM/yyyy")
                        val date = Date()
                        val curdate = formatter.format(date)
                        var chosendate = b["time"].toString().takeLast(10)

                        if (whentime == "FUTURE"){
                            if (dateComparison(chosendate, curdate)){
                                bookings.add(BookingInformation(
                                    sportcentrename = b["sportcentreName"].toString(),
                                    address = b["address"].toString(),
                                    district = b["district"].toString(),
                                    courtname = b["courtName"].toString(),
                                    time = b["time"].toString(),
                                    timestamp = ts,
                                    bookingid = b["bookingid"].toString(),
                                    courtId = b["courtId"].toString()
                                ))
                            }
                        }
                        else if (whentime == "PAST"){
                            if (!dateComparison(chosendate, curdate)){
                                bookings.add(BookingInformation(
                                    sportcentrename = b["sportcentreName"].toString(),
                                    address = b["address"].toString(),
                                    district = b["district"].toString(),
                                    courtname = b["courtName"].toString(),
                                    time = b["time"].toString(),
                                    timestamp = ts,
                                    bookingid = b["bookingid"].toString(),
                                    courtId = b["courtId"].toString()
                                ))
                            }
                        }
                    }
                    bookings.sortWith(compareBy<BookingInformation> { it.getTime().toString()
                        .takeLast(10).slice(6..9) }
                        .thenBy { it.getTime().toString().takeLast(10).slice(3..4)}
                        .thenBy { it.getTime().toString().takeLast(10).slice(0..1)}
                        .thenBy { it.getTime().toString().split(" at ")[0].split(" - ")[0].split(":")[0].toInt() })
                    when (bookingListActivity) {
                        is BookingListActivity -> {
                            // if no future booking
                            if (whentime == "FUTURE" && bookings.size == 0){
                                bookingListActivity.noFutureBooking()
                            }
                            // if no past booking
                            else if (whentime == "PAST" && bookings.size == 0){
                                bookingListActivity.noPastBooking()
                            }
                            else{
                                bookingListActivity.retrieveBookingSuccess(bookings, whentime)
                            }
                        }
                    }
                }

            }
    }

    fun addBookmark(sportctr: SportCentre, activity: Activity){
        // Create new collection, if not exists
        var userBookmarkRef = mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .collection("Bookmark")

        // check if document exists in this collection
        userBookmarkRef.whereEqualTo("done", false).get().addOnCompleteListener {
            if (it.isSuccessful) {
                // set data
                userBookmarkRef.document(sportctr.getCourtId())
                    .set(sportctr)
                    .addOnSuccessListener {
                        // increment bookmark value in sportvenue
                        FirebaseFirestore.getInstance().collection("AllCourt")
                            .document(sportctr.getDistrict())
                            .collection("SportCentre")
                            .document(sportctr.getCourtId())
                            .update("bookmarks" , FieldValue.increment(1))
                            .addOnSuccessListener {
                                when(activity){
                                    is BookingActivity -> {
                                        Common.bookmarkArray.add(sportctr)
                                        activity.addBookmarkSuccess()
                                    }
                                    is PlayerCountActivity -> {
                                        Common.bookmarkArray.add(sportctr)
                                        activity.addBookmarkSuccess()
                                    }
                                }
                            }

                    }
            }
        }
    }

    fun addBookmarkToCourt(){

    }

    fun getAllBookmark(bookmactivity: Activity){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .collection("Bookmark")
            .get()
            .addOnSuccessListener { bookmark_doc ->
                if (!bookmark_doc.isEmpty) {
                    var bookmarks = ArrayList<SportCentre>()
                    for (doc in bookmark_doc) {
                        bookmarks.add(
                            SportCentre(
                                Name_en = doc.data["name"].toString(),
                                Address_en = doc.data["address"].toString(),
                                court_id = doc.id,
                                District_en = doc.data["district"].toString(),
                                Phone = doc.data["phone"].toString(),
                                Opening_hours_en = doc.data["opening_hours_en"].toString(),
                                Ancillary_facilities_en = doc.data["facility"].toString()
                            )
                        )
                    }
                    Common.bookmarkArray = bookmarks

                }
                when (bookmactivity) {
                    is BookmarkRecomActivity -> {
                        bookmactivity.retrieveBookmarkSuccess()
                    }
                }
            }
    }

    fun deleteBookmark(sportCentre: SportCentre, activity: Activity) {
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .document(FirestoreClass().getCurrentUserID())
            .collection("Bookmark")
            .document(sportCentre.getCourtId())
            .delete()
            .addOnFailureListener { e ->
                Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()

            }
            .addOnSuccessListener {
                // decrement bookmarks value in sportcentre
                FirebaseFirestore.getInstance().collection("AllCourt")
                    .document(sportCentre.getDistrict())
                    .collection("SportCentre")
                    .document(sportCentre.getCourtId())
                    .update("bookmarks" , FieldValue.increment(-1))
                    .addOnSuccessListener {
                        Common.bookmarkArray.remove(sportCentre)
                        Toast.makeText(activity, "Success delete from bookmark!", Toast.LENGTH_SHORT)
                            .show()
                        when(activity){
                            is BookingActivity -> {
                                activity.removeBookmarkSuccess()
                            }
                            is PlayerCountActivity -> {
                                activity.removeBookmarkSuccess()
                            }
                            is BookmarkRecomActivity -> {
                                activity.startActivity(Intent(activity, BookmarkRecomActivity::class.java))
                            }
                        }
                    }

            }
    }

//    fun getBookmark(sportCentre: SportCentre){
//        FirebaseFirestore.getInstance()
//            .collection("AllCourt")
//            .document(sportCentre.getDistrict())
//            .collection("SportCentre")
//            .document(sportCentre.getCourtId().get()
//
//    }


}