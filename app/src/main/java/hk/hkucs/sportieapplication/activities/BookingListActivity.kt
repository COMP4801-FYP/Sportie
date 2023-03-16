package hk.hkucs.sportieapplication.activities

import android.Manifest.permission.READ_CALENDAR
import android.Manifest.permission.WRITE_CALENDAR
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.karumi.dexter.Dexter
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.adapter.BookingListAdapter
import hk.hkucs.sportieapplication.databinding.ActivityBookingListBinding
import hk.hkucs.sportieapplication.firestore.FirestoreClass
import hk.hkucs.sportieapplication.models.BookingInformation

class BookingListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize binding class with contentView as root
        binding = ActivityBookingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

//
//        Dexter.withActivity(this)
//            .withPermissions(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)

        binding.addBookingBtn.setOnClickListener {
            val intent = Intent(this, BookingActivity::class.java)
            startActivity(intent)
        }

        binding.bookingListView.isClickable = true
        FirestoreClass().getBookingList(this)



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
    fun retrieveBookingSuccess(bookingListInput: ArrayList<BookingInformation>) {
        binding.bookingListView.adapter = BookingListAdapter(this, bookingListInput)

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
}

