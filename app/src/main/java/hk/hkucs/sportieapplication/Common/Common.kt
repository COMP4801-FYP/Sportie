package hk.hkucs.sportieapplication.Common

import hk.hkucs.sportieapplication.models.Court
import hk.hkucs.sportieapplication.models.SportCentre
import hk.hkucs.sportieapplication.models.User

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

        val TIME_SLOT_TOTAL: Int = 11
        var district: String? = null
        var currentUser: User? = null
        var currentSportCentre: SportCentre? = null
        var currentCourt: Court? = null
        var step:Int = 0 // first step is 0
    }
}