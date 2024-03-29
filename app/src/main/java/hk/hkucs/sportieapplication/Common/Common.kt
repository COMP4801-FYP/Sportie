package hk.hkucs.sportieapplication.Common

import android.location.Location
import com.google.firebase.Timestamp
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.models.*
import java.lang.Math.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Common {
    companion object {
        fun convertTimeSlotToString(slot: Int): String {

            var opentime = Common.currentSportCentre!!.getopenhour()
            var otime = opentime.split(" - ")[0]
            var ctime = opentime.split(" - ")[1]

            var k:Int
            // if no time data
            if (otime == ""){
                otime = "08"
                k = 12
            }
            else{
                k = ctime.slice(0..1).toInt() - otime.slice(0..1).toInt() - 1
                if (otime.slice(3..4) != "00"){
                    k -= 1
                }
            }

            var tmplist = mutableListOf<String>()
            for (i in 0..k){
                var open = otime.slice(0..1).toInt() + i
                var close = otime.slice(0..1).toInt() + i + 1
                var openstr = "$open:${otime.slice(3..4)}"
                var closestr = "$close:${otime.slice(3..4)}"
                tmplist.add("$openstr - $closestr")
            }

            return tmplist[slot]
//            when (slot){
//                0 -> return "8:00 - 9:00"
//                1 -> return "9:00 - 10:00"
//                2 -> return "10:00 - 11:00"
//                3 -> return "11:00 - 12:00"
//                4 -> return "12:00 - 13:00"
//                5 -> return "13:00 - 14:00"
//                6 -> return "15:00 - 16:00"
//                7 -> return "16:00 - 17:00"
//                8 -> return "17:00 - 18:00"
//                9 -> return "18:00 - 19:00"
//                10 -> return "19:00 - 20:00"
//                else -> return "Closed"
//            }
        }

        fun convertTimeStampToStringKey(timestamp: Timestamp):String{
            var data: Date = timestamp.toDate()
            var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd_MM_yyyy")
            return simpleDateFormat.format((data))
        }

//        var currentDate: Calendar = Calendar.getInstance()
        var bookingDate:Calendar = Calendar.getInstance()
//        val TIME_SLOT_TOTAL: Int = 11
        var district: String? = null
        var currentUser: User? = null
        var currentTimeSlot: Int = -1
        var currentSportCentre: SportCentre? = null
        var currentCourt: Court? = null
        var step:Int = 0 // first step is 0
        var simpleDateFormat = SimpleDateFormat("dd_MM_yyyy")

        var bookmarkArray: ArrayList<SportCentre> = ArrayList()

        var ALL_DISTRICT:String = "Select District"

        var allCentreArray: ArrayList<SportCentre> = ArrayList()
        var allDistrictArray: ArrayList<String> = ArrayList()

        var tmpCentreForFilter: ArrayList<SportCentre> = ArrayList()

        var curlatitude = 22.283141
        var curlongitude = 114.137676




        fun dmsToDd(dms: String): Double {
            val (degrees, minutes, seconds) = dms.split('-').map { it.toDouble() }
            val dd = degrees + minutes / 60 + seconds / 3600
            return dd
        }

        fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val R = 6371 // Earth's radius in km
            val dLat = (lat2 - lat1).toRadians()
            val dLon = (lon2 - lon1).toRadians()
            val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) + kotlin.math.cos(lat1.toRadians()) * kotlin.math.cos(
                lat2.toRadians()
            ) * kotlin.math.sin(
                dLon / 2
            ) * kotlin.math.sin(
                dLon / 2
            )
            val c = 2 * kotlin.math.atan2(sqrt(a), sqrt(1 - a))
            val d = R * c
            return d
        }

        fun Double.toRadians(): Double {
            return this * PI / 180
        }

        fun distance2(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
            val location1 = Location("")
            location1.latitude = lat1
            location1.longitude = lon1

            val location2 = Location("")
            location2.latitude = lat2
            location2.longitude = lon2

            return location1.distanceTo(location2)
        }

        fun getsorterlist():ArrayList<Sorter>{
            var sorterlist = ArrayList<Sorter>()

            val azsorter = Sorter(name = "A-Z", image = R.drawable.ic_baseline_sort_by_alpha_24)
            sorterlist.add(azsorter)

            val occsorter = Sorter(name = "Occupancy", image = R.drawable.occupancy_icon_yellow)
            sorterlist.add(occsorter)

            val distancesorter = Sorter(name = "Distance", image = R.drawable.ic_baseline_location_on_24_yellow)
            sorterlist.add(distancesorter)

            val bookmarksorter = Sorter(name = "Bookmark", image = R.drawable.ic_baseline_bookmarks_24)
            sorterlist.add(bookmarksorter)

            return sorterlist
        }


    }
}