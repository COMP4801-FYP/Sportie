package hk.hkucs.sportieapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.activities.BookingListActivity
import hk.hkucs.sportieapplication.activities.SignInActivity
import hk.hkucs.sportieapplication.models.BookingInformation
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BookingListAdapter(context: Context, val bookingList: ArrayList<BookingInformation>): ArrayAdapter<BookingInformation>(context, R.layout.layout_list_booking_item, bookingList)  {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.layout_list_booking_item, null)

        val bookingno: TextView = view.findViewById(R.id.bookingno)

        val sportctrname: TextView = view.findViewById(R.id.txt_booking_sportctr)
        val bookingAddr: TextView = view.findViewById(R.id.txt_booking_address)
        val bookingCourt: TextView = view.findViewById(R.id.txt_booking_court)
        val bookingDate: TextView = view.findViewById(R.id.txt_booking_date)
        val bookingTime: TextView = view.findViewById(R.id.txt_booking_time)
        val bookingDistrict: TextView = view.findViewById(R.id.txt_booking_district)

        bookingno.text = bookingno.text.toString() + " " + (position+1)
        bookingAddr.text = bookingList[position].getAddress()
        bookingCourt.text = bookingList[position].getCourtName()
        bookingDate.text = bookingList[position].getTime().takeLast(10)
        bookingTime.text = bookingList[position].getTime().substringBefore("at")
        sportctrname.text = bookingList[position].getSportcentreName()
        bookingDistrict.text = bookingList[position].getDistrict()

        val delbtn: Button = view.findViewById(R.id.delBtn)
        val modifybtn: Button = view.findViewById(R.id.modifyBtn)

        delbtn.setOnClickListener{
            println("yo" + bookingList[position].getCourtName())
            deleteBookingFromCourt(bookingList[position])
        }

        modifybtn.setOnClickListener{

        }

        return view
    }

    private fun deleteBookingFromCourt(bookinfo: BookingInformation) {
        var courtbookinginfo = FirebaseFirestore.getInstance()
            .collection("AllCourt")
            .document(bookinfo.getDistrict())
            .collection("SportCentre")
            .document(bookinfo.getCourtId().substringBefore("_"))
            .collection("Court")
            .document(bookinfo.getCourtId())
            .collection(bookinfo.getTime().takeLast(10).replace('/','_'))
            .document(bookinfo.getSlot().toString())

        courtbookinginfo.delete().addOnFailureListener{ e->
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()

        }
            .addOnSuccessListener {
                println("hia" + bookinfo.getTime().takeLast(10).replace('/','_'))
                Toast.makeText(context, "Success delete from court!", Toast.LENGTH_SHORT).show()
                deleteBookingFromUser(bookinfo)
            }
    }

    private fun deleteBookingFromUser(bookinfo: BookingInformation) {
        var userbookinginfo = FirebaseFirestore.getInstance()
            .collection("user")
            .document(Common.currentUser!!.userid)
            .collection("Booking")
            .document(bookinfo.getBookingid())

        userbookinginfo.delete().addOnFailureListener{ e->
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
            .addOnSuccessListener {
                Toast.makeText(context, "Success delete booking!", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, BookingListActivity::class.java))
            }
    }
}

