package hk.hkucs.sportieapplication.fragment

import android.app.AlertDialog
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.HorizontalCalendarView
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import dmax.dialog.SpotsDialog
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.Common.SpacesItemDecoration
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.`interface`.ITimeSlotListener
import hk.hkucs.sportieapplication.adapter.CourtAdapter
import hk.hkucs.sportieapplication.adapter.TimeSlotAdapter
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepThreeBinding
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepTwoBinding
import hk.hkucs.sportieapplication.models.Court
import hk.hkucs.sportieapplication.models.TimeSlot
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class BookingStep3Fragment:Fragment(), ITimeSlotListener {
    private lateinit var binding: FragmentBookingStepThreeBinding
    private lateinit var localBroadcastManager: LocalBroadcastManager
    lateinit var iTimeSlotLoadListener: ITimeSlotListener
    lateinit var dialog: AlertDialog

    lateinit var courtRef:DocumentReference

    lateinit var recycler_time_slot: RecyclerView
    lateinit var calendarView: HorizontalCalendarView
    lateinit var simpleDateFromat: SimpleDateFormat
    private lateinit var sportctrname: TextView
    private lateinit var sportcourtno: TextView


    private val displayTimeSlot: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context, intent: Intent){
            var date = Calendar.getInstance()
            date.add(Calendar.DATE, 0) // Add current date
            loadAvailableTimeSlotOfCourt(Common.currentCourt!!.getCourtId(),simpleDateFromat.format(date.time))
            sportctrname.text = Common.currentSportCentre!!.getName()
            sportcourtno.text = Common.currentCourt!!.getName()

        }
    }

    private fun loadAvailableTimeSlotOfCourt(courtId: String, bookDate: String) {
        dialog.show()
        courtRef = FirebaseFirestore.getInstance().collection("AllCourt")
            .document(Common.currentSportCentre!!.getDistrict())
            .collection("SportCentre")
            .document(Common.currentSportCentre!!.getCourtId())
            .collection("Court")
            .document(Common.currentCourt!!.getCourtId())

        // get info of the court
        courtRef.get().addOnSuccessListener() {
            // if court available
            documents ->
                if (documents.exists()) {
                    // get info of booking, return empty if not created
                    var date = FirebaseFirestore.getInstance().collection("AllCourt")
                        .document(Common.district!!)
                        .collection("SportCentre")
                        .document(Common.currentSportCentre!!.getCourtId())
                        .collection("Court")
                        .document(Common.currentCourt!!.getCourtId())
                        .collection(bookDate)

                    date.get().addOnSuccessListener {
                        documents ->
                            // no booking
                            if (documents.isEmpty) {
                                iTimeSlotLoadListener.onTimeSlotLoadEmpty()
                            }
                            // if have booking
                            else {
                                var timesSlots = ArrayList<TimeSlot>()
                                for (doc in documents) {
                                    timesSlots.add(doc.toObject(TimeSlot::class.java))
                                }
                                iTimeSlotLoadListener.onTimeSlotLoadSuccess(timesSlots)
                            }
                        }
                    .addOnFailureListener {
                        e -> iTimeSlotLoadListener.onTimeSlotLoadFailed(e.message.toString())
                    }
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        iTimeSlotLoadListener = this

        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext())
        localBroadcastManager.registerReceiver(displayTimeSlot, IntentFilter("DISPLAY_TIME_SLOT"))

        simpleDateFromat = SimpleDateFormat("dd_MM_yyyy") // 19_01_2023

        dialog = SpotsDialog.Builder().setContext(requireContext()).setCancelable(false).build()

    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot)
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookingStepThreeBinding.inflate(layoutInflater)

        recycler_time_slot = binding.recyclerTimeSlot

        calendarView =  binding.calendarView

        sportctrname =  binding.sportCtrName

        sportcourtno = binding.sportCourtNo

        init(binding.root)

        return binding.root
    }

    fun init(itemView:View){
        recycler_time_slot.setHasFixedSize(true)
        recycler_time_slot.layoutManager = GridLayoutManager(requireActivity(), 2) // 3 columns
        recycler_time_slot.addItemDecoration(SpacesItemDecoration(8))

        // Calendar
        var startDate = Calendar.getInstance()
        startDate.add(Calendar.DATE,0)
        var endDate = Calendar.getInstance()
        endDate.add(Calendar.DATE, 13) // 2 day left


        var horizontalCalendarView = HorizontalCalendar.Builder(itemView, R.id.calendarView)
            .range(startDate,endDate)
            .datesNumberOnScreen(5)
            .mode(HorizontalCalendar.Mode.DAYS)
            .defaultSelectedDate(startDate)
            .build()

        horizontalCalendarView.calendarListener = object :HorizontalCalendarListener(){
            override fun onDateSelected(date: Calendar?, position: Int) {
                if(Common.bookingDate.timeInMillis != date!!.timeInMillis){
                    // code wont run again if select new day same with selected day
                    Common.bookingDate = date
                    loadAvailableTimeSlotOfCourt(Common.currentCourt!!.getCourtId(),simpleDateFromat.format(date.time))
                }
            }
        }
    }

    override fun onTimeSlotLoadSuccess(timeSlotList: ArrayList<TimeSlot>) {
        recycler_time_slot.adapter = TimeSlotAdapter(requireContext(), timeSlotList)
        dialog.dismiss()
    }

    override fun onTimeSlotLoadFailed(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        dialog.dismiss()
    }

    override fun onTimeSlotLoadEmpty() {
        recycler_time_slot.adapter = TimeSlotAdapter(requireContext())
        dialog.dismiss()
    }
}