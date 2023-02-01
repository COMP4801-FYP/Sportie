package hk.hkucs.sportieapplication.activities

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import dmax.dialog.SpotsDialog
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.Common.Common.Companion.ALL_DISTRICT
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.databinding.ActivityBookingBinding
import hk.hkucs.sportieapplication.adapter.MyViewPagerAdapter
import hk.hkucs.sportieapplication.models.Court

class BookingActivity : AppCompatActivity() {
    lateinit var dialog: AlertDialog
    private lateinit var binding:ActivityBookingBinding
    private lateinit var localBroadcastManager:LocalBroadcastManager


    private val buttonNextReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent){
            var step:Int = intent.getIntExtra("STEP", 0)
            if(step == 1){
                Common.currentSportCentre = intent.getParcelableExtra("COURT_SAVE")
            }
            else if(step == 2){
                Common.currentCourt = intent.getParcelableExtra("COURT_SELECTED")
            }

            else if(step == 3){
                Common.currentTimeSlot = intent.getIntExtra("TIME_SLOT",-1)
            }

            binding.btnNextStep.isEnabled = true
            setColorButton()
        }
    }

    override fun onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()

        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(buttonNextReceiver, IntentFilter("ENABLE_BUTTON_NEXT"))

        setupStepView()
        setColorButton()

        binding.viewPager.adapter = MyViewPagerAdapter(this)

        binding.btnPreviousStep.setOnClickListener(){
            if(Common.step == 3 || Common.step > 0){
                Common.step--
                binding.viewPager.currentItem = Common.step
            }
        }

        binding.btnNextStep.setOnClickListener(){
            if(Common.step < 3 || Common.step == 0){
                Common.step++

                //After choose sport centre
                if(Common.step == 1){
                    if(Common.currentSportCentre != null){
                        loadCourtBySportCentre(Common.currentSportCentre!!.getCourtId())
                    }
                }

                // pick time slot
                else if(Common.step == 2){
                    if (Common.currentCourt != null){
                        loadTimeSlotOfBarber(Common.currentCourt!!.getCourtId())
                    }
                }

                // confirmation
                else if(Common.step == 3){
                    if (Common.currentTimeSlot != -1){
                        confirmBooking()
                    }
                }

                binding.viewPager.currentItem = (Common.step)
            }
        }

        var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // show step
                binding.stepView.go(position,true)

                binding.btnPreviousStep.isEnabled = position != 0

                // Set disable button next here
                binding.btnNextStep.isEnabled = false
                setColorButton()
            }
        }
        binding.viewPager.registerOnPageChangeCallback(myPageChangeCallback)

        // prevent swiping in viewpager
        binding.viewPager.isUserInputEnabled = false
        // 4 fragments, keep state of this 4 screen page, if not will lose state of all view when press previous
        binding.viewPager.offscreenPageLimit = 4


        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.booking

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.booking -> {
                    return@setOnItemSelectedListener true
                }
//                R.id.groups -> {
//                    startActivity(Intent(applicationContext, GroupsActivity::class.java))
//                    overridePendingTransition(0, 0)
//                    return@setOnItemSelectedListener true
//                }
//                R.id.home -> {
//                    startActivity(Intent(applicationContext, MainActivity::class.java))
//                    overridePendingTransition(0, 0)
//                    return@setOnItemSelectedListener true
//                }
//                R.id.friends -> {
//                    startActivity(Intent(applicationContext, FriendsActivity::class.java))
//                    overridePendingTransition(0, 0)
//                    return@setOnItemSelectedListener true
//                }
                R.id.profile -> {
                    startActivity(Intent(applicationContext, UserProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun confirmBooking() {
        // send broadcast to fragment step four
        val intent = Intent("CONFIRM_BOOKING")
        localBroadcastManager.sendBroadcast(intent)
    }

    private fun loadTimeSlotOfBarber(courtId: String) {
        // send local broadcast to Fragment step 3
        val intent = Intent("DISPLAY_TIME_SLOT")
        localBroadcastManager.sendBroadcast(intent)
    }

    private fun loadCourtBySportCentre(courtId: String) {
        dialog.show()
        // Select all court of the Sport centre
        if(!TextUtils.isEmpty(Common.district)){

            FirebaseFirestore.getInstance()
                .collection("AllCourt")
                .document(Common.district!!)
                .collection("SportCentre")
                .document(courtId)
                .collection("Court")
                .get()
                .addOnSuccessListener{ documents ->
                    if (!documents.isEmpty) {
                        var courtArray = ArrayList<Court>()
                        for (document in documents) {
                            courtArray.add(Court(document.data["name"].toString(), document.data["address"].toString(), document.id))
                        }
                        // send broadcast to BookingStep2Fragment to load Recycler
                        val intent = Intent("COURT_LOAD_DONE")
                        intent.putParcelableArrayListExtra("COURT_LOAD_DONE", courtArray)
                        localBroadcastManager.sendBroadcast(intent)
                        dialog.dismiss()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(
                        this.javaClass?.simpleName,
                        "Error while loading court in SPORTCENTRE.", e
                    )
                    dialog.dismiss();
                }
        }
        else{
            dialog.dismiss();
        }
    }

    private fun setColorButton() {
        if (binding.btnNextStep.isEnabled){
            binding.btnNextStep.setBackgroundResource(R.color.colorPrimaryText)
        }
        else{
            binding.btnNextStep.setBackgroundResource(R.color.light_grey)
        }
        if (binding.btnPreviousStep.isEnabled){
            binding.btnPreviousStep.setBackgroundResource(R.color.colorPrimaryText)
        }
        else{
            binding.btnPreviousStep.setBackgroundResource(R.color.light_grey)
        }
    }

    private fun setupStepView(){
        val stepList = listOf("Sport Centre","Court", "Time", "Confirm")
        binding.stepView.setSteps(stepList)
    }
}