package hk.hkucs.sportieapplication.Common

import com.google.firebase.Timestamp
import hk.hkucs.sportieapplication.models.Court
import hk.hkucs.sportieapplication.models.SportCentre
import hk.hkucs.sportieapplication.models.TimeSlot
import hk.hkucs.sportieapplication.models.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Common {
    companion object {
        fun convertTimeSlotToString(slot: Int): String {
            when (slot){
                0 -> return "8:00 - 9:00"
                1 -> return "9:00 - 10:00"
                2 -> return "10:00 - 11:00"
                3 -> return "11:00 - 12:00"
                4 -> return "12:00 - 13:00"
                5 -> return "13:00 - 14:00"
                6 -> return "15:00 - 16:00"
                7 -> return "16:00 - 17:00"
                8 -> return "17:00 - 18:00"
                9 -> return "18:00 - 19:00"
                10 -> return "19:00 - 20:00"
                else -> return "Closed"
            }
        }

        fun convertTimeStampToStringKey(timestamp: Timestamp):String{
            var data: Date = timestamp.toDate()
            var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd_MM_yyyy")
            return simpleDateFormat.format((data))
        }

//        var currentDate: Calendar = Calendar.getInstance()
        var bookingDate:Calendar = Calendar.getInstance()
        val TIME_SLOT_TOTAL: Int = 11
        var district: String? = null
        var currentUser: User? = null
        var currentTimeSlot: Int = -1
        var currentSportCentre: SportCentre? = null
        var currentCourt: Court? = null
        var step:Int = 0 // first step is 0
        var simpleDateFormat = SimpleDateFormat("dd_MM_yyyy")

        var ALL_DISTRICT:String = "Please choose district"

        var allCentreArray: ArrayList<SportCentre> = ArrayList()
    }
}