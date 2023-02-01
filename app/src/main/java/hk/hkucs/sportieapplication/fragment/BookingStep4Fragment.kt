package hk.hkucs.sportieapplication.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CpuUsageInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepFourBinding
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepThreeBinding
import hk.hkucs.sportieapplication.models.BookingInformation
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

    private val confirmBookingReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context, intent: Intent){
            txt_booking_username.setText(Common.currentUser!!.lastName + " " + Common.currentUser!!.firstName)
            txt_booking_court_text.setText(Common.currentCourt!!.getName())
            txt_booking_time_text.setText(java.lang.StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" at ")
                .append(simpleDateFormat.format(Common.currentDate.getTime())))


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
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver)
        super.onDestroy()
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
//            var bookInfo = BookingInformation(
//                sportcentreId = Common.currentSportCentre!!.court_id,
//                sportcentrename = Common.currentSportCentre!!.Name_en,
//                username = Common.currentUser!!.lastName + Common.currentUser!!.firstName,
//                userphone = Common.currentUser!!.mobile
//
//            )
        }

//        init(binding.root)

        return binding.root
    }
}