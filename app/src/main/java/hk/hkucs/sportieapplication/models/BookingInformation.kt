package hk.hkucs.sportieapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
class BookingInformation(
    private var username: String = "",
    private var userphone: Long = 0,
    private var time: String = "",
    private var sportcentreId: String = "",
    private var sportcentrename: String = "",
    private var courtId: String= "",
    private var courtname: String= "",
    private var address: String ="",
    private var district: String="",
    private var slot: Long = 0,
    private var done: Boolean = false,
    private var timestamp:com.google.firebase.Timestamp,
    private var bookingid: String = ""
): Parcelable {
    fun getUsername(): String {
        return username
    }

    fun getAddress(): String {
        return address
    }

    fun getCourtId(): String {
        return courtId
    }

    fun getTime(): String {
        return time
    }

    fun getSlot(): Long {
        return slot
    }

    fun getDistrict(): String {
        return district
    }

    fun getCourtName(): String {
        return courtname
    }

    fun getSportcentreName(): String {
        return sportcentrename
    }

    fun getBookingid(): String {
        return bookingid
    }

//    fun isDone(): Boolean {
//        return done
//    }

//    fun getTimestamp(): Timestamp {
//        return timestamp
//    }
}