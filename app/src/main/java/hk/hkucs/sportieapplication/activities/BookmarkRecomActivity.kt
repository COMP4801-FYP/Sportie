package hk.hkucs.sportieapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.adapter.BookingListAdapter
import hk.hkucs.sportieapplication.adapter.BookmarkAdapter
import hk.hkucs.sportieapplication.databinding.ActivityBookingListBinding
import hk.hkucs.sportieapplication.databinding.ActivityBookmarkRecomBinding
import hk.hkucs.sportieapplication.firestore.FirestoreClass
import hk.hkucs.sportieapplication.models.BookingInformation
import hk.hkucs.sportieapplication.models.SportCentre

class BookmarkRecomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarkRecomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // initialize binding class with contentView as root
        binding = ActivityBookmarkRecomBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.addBookingBtn.setOnClickListener {
            finish()
            val intent = Intent(this, BookingActivity::class.java)
            startActivity(intent)
        }

        binding.bookingListView.isClickable = true
        FirestoreClass().getBookmark(this)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.friends

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.booking -> {
                    startActivity(Intent(applicationContext, BookingListActivity::class.java))
                    overridePendingTransition(0, 0)
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
    fun retrieveBookmarkSuccess() {
        Common.bookmarkArray.sortWith(compareBy<SportCentre> { it.getName()})
        binding.bookingListView.adapter = BookmarkAdapter(this, Common.bookmarkArray)
    }
}