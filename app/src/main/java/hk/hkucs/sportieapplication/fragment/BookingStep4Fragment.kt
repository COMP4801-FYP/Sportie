package hk.hkucs.sportieapplication.fragment

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CpuUsageInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dmax.dialog.SpotsDialog
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.activities.BookingListActivity
import hk.hkucs.sportieapplication.activities.SignInActivity
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepFourBinding
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepThreeBinding
import hk.hkucs.sportieapplication.models.BookingInformation
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class BookingStep4Fragment:Fragment() {
    lateinit var simpleDateFormat:SimpleDateFormat
    private lateinit var binding: FragmentBookingStepFourBinding
    private lateinit var localBroadcastManager: LocalBroadcastManager
    lateinit var txt_booking_username:TextView
    lateinit var txt_booking_court_text:TextView
    lateinit var txt_booking_time_text:TextView
    lateinit var txt_sportctr_address:TextView
    lateinit var txt_sportctr_name:TextView
    lateinit var txt_sportctr_phone:TextView
    lateinit var txt_sportctr_open_hours:TextView
    lateinit var dialog: AlertDialog

    //keys for reCaptcha verification
    private val siteKey = "6Le6nk0lAAAAALuHw-P71TEFLYAb1zFZEs9UT-Ql"
    private val secretKey = "6Le6nk0lAAAAAO-U95uPZR71Lg1TH0z6cv89UAsw"
    private lateinit var queue: RequestQueue

    private val confirmBookingReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context, intent: Intent){
            txt_booking_username.setText(Common.currentUser!!.lastName + " " + Common.currentUser!!.firstName)
            txt_booking_court_text.setText(Common.currentCourt!!.getName())
            txt_booking_time_text.setText(java.lang.StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" at ")
                .append(simpleDateFormat.format(Common.bookingDate.getTime())))


            txt_sportctr_address.text = Common.currentSportCentre!!.Address_en
            txt_sportctr_open_hours.text = Common.currentSportCentre!!.Opening_hours_en
            txt_sportctr_name.text = Common.currentSportCentre!!.Name_en
            txt_sportctr_phone.text = Common.currentSportCentre!!.Phone
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Apply format for date display on Confirm
        simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext())
        localBroadcastManager.registerReceiver(confirmBookingReceiver, IntentFilter("CONFIRM_BOOKING"))

        dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()

        queue = Volley.newRequestQueue(activity?.applicationContext)

    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver)
        super.onDestroy()
    }

    private fun launchRecaptcha() {
        SafetyNet.getClient(requireActivity()).verifyWithRecaptcha(siteKey)
            .addOnSuccessListener {
                Log.d("INFO","Success launch recaptcha")
                handleSuccess(it)
            }
            .addOnFailureListener {
                Log.d("INFO","Failure launch recaptcha")
                handleFailure(it)
            }
    }

    private fun handleSuccess(recaptchaTokenResponse: SafetyNetApi.RecaptchaTokenResponse) {
        if (recaptchaTokenResponse.tokenResult.isNotEmpty()) {
            Log.d("INFO","Handle success function called")
            handleSiteVerification(recaptchaTokenResponse.tokenResult)
        }
    }

    private fun handleFailure(exception: Exception) {
        if (exception is ApiException) {
            Log.d("INFO","Handle failure function called")
            Log.d(
                "INFO",
                "Error message: " + CommonStatusCodes.getStatusCodeString(exception.statusCode)
            )
        } else {
            Log.d("INFO", "Unknown type of error: " + exception.message)
        }
    }

    private fun handleSiteVerification(tokenResult: String) {
        Log.d("INFO","Handle site verification function called")
        val url = "https://www.google.com/recaptcha/api/siteverify"
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response -> handleResponse(response) },
            Response.ErrorListener { error -> Log.d("INFO", error.message.toString()) }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["secret"] = secretKey
                params["response"] = tokenResult
                return params
            }
        }

        request.retryPolicy = DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        queue.add(request)
    }

    private fun handleResponse(response: String) {
        try
        {
            val jsonObject = JSONObject(response)
            if (jsonObject.getBoolean("success")) {
                Log.d("INFO","Captcha verification successful")
                dialog.show()

                //Process timestamp to display future booking only
                var startTime = Common.convertTimeSlotToString(Common.currentTimeSlot)
                var convertTime = startTime.split("-") // ex: 9:00 - 10:00


                var startTimeConvert = convertTime[0].split(":")
                var startHourInt = Integer.parseInt(startTimeConvert[0].trim())
                var startMinInt = Integer.parseInt(startTimeConvert[1].trim())

                var bookingDateWithoutHour = Calendar.getInstance()
                bookingDateWithoutHour.timeInMillis = Common.bookingDate.timeInMillis
                bookingDateWithoutHour.set(Calendar.HOUR_OF_DAY,startHourInt)
                bookingDateWithoutHour.set(Calendar.MINUTE,startMinInt)

                // create timestamp object and apply to bookinginformation
                var timestamp = Timestamp(bookingDateWithoutHour.time)

                var bookInfo = BookingInformation(
                    timestamp = timestamp,
                    done = false,
                    sportcentreId = Common.currentSportCentre!!.court_id,
                    sportcentrename = Common.currentSportCentre!!.Name_en,
                    username = Common.currentUser!!.lastName + Common.currentUser!!.firstName,
                    userphone = Common.currentUser!!.mobile,
                    courtId = Common.currentCourt!!.getCourtId(),
                    courtname = Common.currentCourt!!.getName(),
                    address = Common.currentSportCentre!!.getAddress(),
                    district = Common.district!!,
                    time = java.lang.StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                        .append(" at ")
                        .append(simpleDateFormat.format(bookingDateWithoutHour.getTime())).toString(),
                    slot = Common.currentTimeSlot.toLong(),
                    bookingid = Common.currentCourt!!.getCourtId() + "_" + Common.simpleDateFormat.format(Common.bookingDate.time) + "_" + Common.currentTimeSlot.toString()
                )

                // submit
                var date = FirebaseFirestore.getInstance().collection("AllCourt")
                    .document(Common.district!!)
                    .collection("SportCentre")
                    .document(Common.currentSportCentre!!.getCourtId())
                    .collection("Court")
                    .document(Common.currentCourt!!.getCourtId())
                    .collection(Common.simpleDateFormat.format(Common.bookingDate.time))
                    .document(Common.currentTimeSlot.toString())

                // write data
                date.set(bookInfo)
                    .addOnSuccessListener{
                        // check if a booking exists, to prevent new booking
                        addToUserBooking(bookInfo)
                    }
                    .addOnFailureListener{e->
                        Toast.makeText(context, e.message ,Toast.LENGTH_SHORT).show()
                    }
            }
            else
            {
                Toast.makeText(
                    activity?.applicationContext,
                    jsonObject.getString("error-codes").toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        catch (ex: Exception)
        {
            Log.d("INFO", "JSON exception: " + ex.message)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookingStepFourBinding.inflate(layoutInflater)

        txt_booking_username = binding.txtUserText

        txt_booking_court_text = binding.txtBookingCourtText

        txt_booking_time_text =  binding.txtBookingTimeText

        txt_sportctr_address =  binding.txtSportctrAddress

        txt_sportctr_name = binding.txtSportctrName

        txt_sportctr_phone = binding.txtSportctrPhone

        txt_sportctr_open_hours = binding.txtSportctrOpenHours

        binding.btnConfirm.setOnClickListener{
            launchRecaptcha()
        }

        return binding.root
    }

    private fun addToUserBooking(bookInfo: BookingInformation) {
        // Create new collection, if not exists
        var userBookingRef = FirebaseFirestore.getInstance()
            .collection("user")
            .document(Common.currentUser!!.userid)
            .collection("Booking")

        // check if document exists in this collection
        userBookingRef.whereEqualTo("done", false)
            .get()
            .addOnCompleteListener{
                if (it.isSuccessful){
                    // set data
                    userBookingRef.document(bookInfo.getBookingid())
                        .set(bookInfo)
                        .addOnSuccessListener {
                            if(dialog.isShowing){
                                dialog.dismiss()
                            }

                            resetStaticData()
                            requireActivity().finish()
                            Toast.makeText(context, "Booking success!",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireView().context, BookingListActivity::class.java))
                        }
                        .addOnFailureListener{
                            if(dialog.isShowing){
                                dialog.dismiss()
                            }
                            Toast.makeText(context, it.message,Toast.LENGTH_SHORT).show()
                        }
                }
                else{
                    if(dialog.isShowing){
                        dialog.dismiss()
                    }
                    resetStaticData()
                    requireActivity().finish()
                    Toast.makeText(context, "Booking success!",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetStaticData(){
        Common.step = 0
        Common.currentTimeSlot = -1
        Common.currentCourt = null
        Common.currentSportCentre = null
        Common.bookingDate.add(Calendar.DATE, 0) // Curent date
    }
}