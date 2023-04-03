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
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.Common.SpacesItemDecoration
import hk.hkucs.sportieapplication.adapter.CourtAdapter
import hk.hkucs.sportieapplication.adapter.PlayerCountCourtAdapter
import hk.hkucs.sportieapplication.databinding.FragmentBookingStepTwoBinding
import hk.hkucs.sportieapplication.models.Court

class PlayerCountStep2Fragment:Fragment() {
    private lateinit var binding: FragmentBookingStepTwoBinding
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var recycler_court: RecyclerView
    private lateinit var sportctrname: TextView

    private val courtDoneReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context, intent: Intent){
            var courtArrayList = intent.getParcelableArrayListExtra<Court>("COURT_LOAD_DONE")
            var newcourtArrayList = ArrayList<Court>()
            if (courtArrayList != null) {
                for (i in courtArrayList){
                    var i_a =  Court(name = i.getName() + " A", courtId = i.getCourtId(), address = i.getAddress(), playercountA = i.getCourtA().toInt(), playercountB = i.getCourtB().toInt())
                    var i_b=  Court(name = i.getName() + " B", courtId = i.getCourtId(), address = i.getAddress(),playercountA = i.getCourtA().toInt(), playercountB = i.getCourtB().toInt())

                    newcourtArrayList.add(i_a)
                    newcourtArrayList.add(i_b)
                }
            }
            var adapter = PlayerCountCourtAdapter(requireActivity(), newcourtArrayList!!)
            recycler_court.adapter = adapter
            sportctrname.text = Common.currentSportCentre!!.getName()
            binding.colorlegend.visibility = View.VISIBLE
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