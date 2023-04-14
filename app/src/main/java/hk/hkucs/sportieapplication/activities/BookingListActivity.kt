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

        binding.bookingListView.isClickable = true

        binding.addBookingBtn.setOnClickListener {
            finish()
            val intent = Intent(this, BookingActivity::class.java)
            startActivity(intent)
        }


        // get booking list
        FirestoreClass().getBookingList(this, "FUTURE")
        // get bookmark list
        FirestoreClass().getAllBookmark(this)

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
                R.id.friends -> {
                    startActivity(Intent(applicationContext, BookmarkRecomActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
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
        binding.addBookingBtn.visibility = View.GONE
        binding.bookingListView.visibility = View.VISIBLE
        binding.bookingListView.adapter = BookingListAdapter(this, bookingListInput, whentime)
    }

    fun onRadioButtonClicked(view: View){
        var isSelected = (view as AppCompatRadioButton).isChecked
        when (view.id) {
            R.id.rbLeft -> {
                if (isSelected) {
                    FirestoreClass().getBookingList(this, "FUTURE")
                    rbLeft.setTextColor(Color.WHITE)
                    rbRight.setTextColor(getColor(R.color.themeRed))
                }
            }
            R.id.rbRight -> {
                if (isSelected) {
                    FirestoreClass().getBookingList(this, "PAST")
                    rbRight.setTextColor(Color.WHITE)
                    rbLeft.setTextColor(getColor(R.color.themeRed))
                }
            }
        }
    }

    fun noFutureBooking() {
        binding.addBookingBtn.visibility = View.VISIBLE
        binding.bookingListView.visibility = View.GONE
    }

    fun noPastBooking() {
        binding.addBookingBtn.visibility = View.VISIBLE
        binding.bookingListView.visibility = View.GONE
    }

}

