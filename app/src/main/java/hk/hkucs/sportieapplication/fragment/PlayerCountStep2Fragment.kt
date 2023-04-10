package hk.hkucs.sportieapplication.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.Common.SpacesItemDecoration
import hk.hkucs.sportieapplication.adapter.CourtAdapter
import hk.hkucs.sportieapplication.adapter.PlayerCountCourtAdapter
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepTwoBinding
import hk.hkucs.sportieapplication.models.Court
import hk.hkucs.sportieapplication.models.SportCentre
import hk.hkucs.sportieapplication.models.TimeSlot
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class PlayerCountStep2Fragment:Fragment() {
    private lateinit var binding: FragmentBookingStepTwoBinding
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var recycler_court: RecyclerView
    private lateinit var sportctrname: TextView

    private val courtDoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context, intent: Intent){
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy")
            val formattedDate = currentDate.format(formatter)
            println("today $formattedDate")

            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            println("current hour $currentHour")

            var courtArrayList = intent.getParcelableArrayListExtra<Court>("COURT_LOAD_DONE")

            // create Court A and B for each  Court
            var newcourtArrayList = ArrayList<Court>()
            if (courtArrayList != null) {
                for (i in courtArrayList){
                    var bookingInfoStr = ""
                    FirebaseFirestore.getInstance().collection("AllCourt")
                        .document(Common.currentSportCentre!!.getDistrict())
                        .collection("SportCentre")
                        .document(Common.currentSportCentre!!.getCourtId())
                        .collection("Court")
                        .document(i.getCourtId())
                        .collection(formattedDate)
                        .get()
                        .addOnSuccessListener { documents ->
                            // no booking
                            if (documents.isEmpty) {
                                bookingInfoStr = "No Booking for this court today"
                            }
                            // if have booking
                            else {
                                var timesSlots = java.util.ArrayList<Int>()

                                var consecutiveHours = 0
                                for (doc in documents) {
                                    var openhour = java.lang.StringBuilder(Common.convertTimeSlotToString(doc.toObject(TimeSlot::class.java).getSlot().toInt())).split(" ")[0].split(":")[0].toInt()
                                    if (openhour == currentHour){
                                        timesSlots.add(openhour)
                                        consecutiveHours = 1
                                    }
                                    else if (openhour > currentHour){
                                        timesSlots.add(openhour)
                                    }
                                }
                                println(timesSlots)
                                var nextNearestHour = 24 // set it to a large value

                                // find consecutive hours
                                if (consecutiveHours == 1){
                                    for (i in timesSlots.indices) {
                                        if (i > 0){
                                            if (timesSlots[i] - timesSlots[i - 1] == 1) {
                                                consecutiveHours += 1
                                            } else {
                                                break
                                            }
                                        }
                                    }
                                    bookingInfoStr = "There are bookings for ${i.getName()} in the next $consecutiveHours hours"
                                }
                                else{
                                    // find next nearest hour
                                    for (hour in timesSlots) {
                                        if (hour > currentHour && hour - currentHour < nextNearestHour) {
                                            nextNearestHour = hour - currentHour
                                        }
                                    }
                                    bookingInfoStr = "There is no booking for ${i.getName()} in the next ${nextNearestHour} hours"
                                }

                                println("next hour $nextNearestHour")
                                println("consec $consecutiveHours")
                                println(bookingInfoStr)
                            }
                            println("bookinfo $bookingInfoStr")
                            var i_a =  Court(name = i.getName() + " A", courtId = i.getCourtId(), address = i.getAddress(), playercountA = i.getCourtA().toInt(), playercountB = i.getCourtB().toInt(), bookingInfo = bookingInfoStr)
                            var i_b=  Court(name = i.getName() + " B", courtId = i.getCourtId(), address = i.getAddress(),playercountA = i.getCourtA().toInt(), playercountB = i.getCourtB().toInt(), bookingInfo = bookingInfoStr)

                            newcourtArrayList.add(i_a)
                            newcourtArrayList.add(i_b)

                            newcourtArrayList!!.sortWith(compareBy<Court> { it.getName()})
                            var adapter = PlayerCountCourtAdapter(requireActivity(), newcourtArrayList!!)
                            recycler_court.adapter = adapter
                            sportctrname.text = Common.currentSportCentre!!.getName()
                            binding.colorlegend.visibility = View.VISIBLE
                        }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext())
        localBroadcastManager.registerReceiver(courtDoneReceiver, IntentFilter("COURT_LOAD_DONE"))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookingStepTwoBinding.inflate(layoutInflater)
//        spinner = binding.spinner
        recycler_court = binding.recyclerCourt

        sportctrname =  binding.sportCtrName


        initView()

        return binding.root
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(courtDoneReceiver)
        super.onDestroy()
    }

    private fun initView() {
        recycler_court.setHasFixedSize(true)
        recycler_court.layoutManager = GridLayoutManager(requireActivity(), 1)
        recycler_court.addItemDecoration(SpacesItemDecoration(4))
    }
}