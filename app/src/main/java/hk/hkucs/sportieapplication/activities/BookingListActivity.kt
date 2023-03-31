package hk.hkucs.sportieapplication.activities

import android.Manifest.permission.READ_CALENDAR
import android.Manifest.permission.WRITE_CALENDAR
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatRadioButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.adapter.BookingListAdapter
import hk.hkucs.sportieapplication.databinding.ActivityBookingListBinding
import hk.hkucs.sportieapplication.firestore.FirestoreClass
import hk.hkucs.sportieapplication.models.BookingInformation

class BookingListActivity : AppCompatActivity() {
    lateinit var rbLeft:AppCompatRadioButton
    lateinit var rbRight:AppCompatRadioButton

    private lateinit var binding: ActivityBookingListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize binding class with contentView as root
        binding = ActivityBookingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rbLeft = binding.rbLeft
        rbRight = binding.rbRight

//        binding.addBookingBtn.setOnClickListener {
//            val intent = Intent(this, BookingActivity::class.java)
//            startActivity(intent)
//        }

        binding.bookingListView.isClickable = true
        FirestoreClass().getBookingList(this, "FUTURE")



        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.booking

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.booking -> {
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
    fun retrieveBookingSuccess(bookingListInput: ArrayList<BookingInformation>, whentime:String) {
        binding.bookingListView.adapter = BookingListAdapter(this, bookingListInput, whentime)

//        binding.bookingListView.setOnItemClickListener { _, _, position, _ ->
//            val billName = billListInput[position].billName
//            val billPayer = billListInput[position].billPayer
//            val billItems = billListInput[position].billItems
//            val billGroup = billListInput[position].billGroup
//            val billGroupName = billListInput[position].billGroupName
//
////            val intent = Intent(this, BillInfoActivity::class.java)
////            intent.putExtra("billName", billName)
////            intent.putExtra("billPayer", billPayer)
////            intent.putExtra("billItems", billItems)
////            intent.putExtra("billGroup", billGroup)
////            intent.putExtra("billGroupName", billGroupName)
////            intent.putExtra("billStatus", billStatus)
////            startActivity(intent)
//        }
    }

    fun onRadioButtonClicked(view: View){
        var isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.rbLeft -> {
                if (isSelected) {
                    FirestoreClass().getBookingList(this, "FUTURE")
                    rbLeft.setTextColor(Color.WHITE)
                    rbRight.setTextColor(getColor(R.color.green3))
                }
            }
            R.id.rbRight -> {
                if (isSelected) {
                    FirestoreClass().getBookingList(this, "PAST")
                    rbRight.setTextColor(Color.WHITE)
                    rbLeft.setTextColor(getColor(R.color.green3))
                }
            }
        }
    }
}

