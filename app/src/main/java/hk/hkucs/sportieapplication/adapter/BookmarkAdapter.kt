package hk.hkucs.sportieapplication.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.activities.BookingActivity
import hk.hkucs.sportieapplication.activities.BookingListActivity
import hk.hkucs.sportieapplication.activities.BookmarkRecomActivity
import hk.hkucs.sportieapplication.activities.SignInActivity
import hk.hkucs.sportieapplication.firestore.FirestoreClass
import hk.hkucs.sportieapplication.models.BookingInformation
import hk.hkucs.sportieapplication.models.SportCentre
import hk.hkucs.sportieapplication.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookmarkAdapter(context: Context, val bookmarkList: ArrayList<SportCentre>): ArrayAdapter<SportCentre>(context, R.layout.layout_list_bookmark_item, bookmarkList)  {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_list_bookmark_item, null)


        val sportctrname: TextView = view.findViewById(R.id.txt_booking_sportctr)
        val bookingAddr: TextView = view.findViewById(R.id.txt_booking_address)
//        val bookingCourt: TextView = view.findViewById(R.id.txt_booking_court)
        val bookingFacility: TextView = view.findViewById(R.id.txt_booking_facility)
        val bookingTime: TextView = view.findViewById(R.id.txt_booking_time)
        val bookingDistrict: TextView = view.findViewById(R.id.txt_booking_district)

        bookingAddr.text = bookmarkList[position].getAddress()
//        bookingCourt.text = bookmarkList[position].getCourtNo()
        bookingFacility.text = bookmarkList[position].getFacility()
        bookingTime.text = bookmarkList[position].getopenhour()
        sportctrname.text = bookmarkList[position].getName()
        bookingDistrict.text = bookmarkList[position].getDistrict()

        val bookbtn: Button = view.findViewById(R.id.bookBtn)
        bookbtn.setOnClickListener{
            Common.currentSportCentre = bookmarkList[position]
            Common.step=1
            var intent = Intent(context, BookingActivity::class.java)
            intent.putExtra("FROM_BOOKMARK", 1)
            context.startActivity(intent)
        }

        val bookremovebtn: ImageButton = view.findViewById(R.id.bookmarkremove)
        bookremovebtn.setOnClickListener{
            var builder = AlertDialog.Builder(context)
            builder.setTitle("Confirm Delete")
            builder.setMessage("Are you sure you want to remove this sport centre from your bookmark? This action cannot be undone.")
            builder.setPositiveButton("Yes",DialogInterface.OnClickListener{ dialog, id ->
                deleteBookmark(bookmarkList[position])
                dialog.cancel()
            })
            builder.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, id ->
                dialog.cancel()
            })
            var alert = builder.create()
            alert.show()
        }


        return view
    }

    private fun deleteBookmark(sportCentre: SportCentre) {
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .document(FirestoreClass().getCurrentUserID())
            .collection("Bookmark")
            .document(sportCentre.getCourtId())
            .delete()
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()

            }
            .addOnSuccessListener {
                Common.bookmarkArray.remove(sportCentre)
                Toast.makeText(context, "Success delete from bookmark!", Toast.LENGTH_SHORT)
                    .show()
                context.startActivity(Intent(context, BookmarkRecomActivity::class.java))
            }
    }

}

