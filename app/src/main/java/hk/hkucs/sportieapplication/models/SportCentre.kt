package hk.hkucs.sportieapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SportCentre(
    var court_id: String = "",
    var District_en: String = "",
    var District_cn: String = "",
    var Name_en: String = "",
    var Name_cn: String = "",
    var Address_en: String = "",
    var Address_cn: String = "",
    var GIHS: String = "",
    var Court_no_en: String = "",
    var Court_no_cn: String = "",
    var Ancillary_facilities_en: String = "",
    var Ancillary_facilities_cn: String = "",
    var Opening_hours_en: String = "",
    var Opening_hours_cn: String = "",
    var Phone: String = "",
    var Remarks_en: String = "",
    var Remarks_cn: String = "",
    var Longitude: String = "",
    var Latitude: String = "",
    var Bookmarks: Int = 0,
    var Occupancy: Int = 0,
    var CurDistance: Double = 0.0

): Parcelable{
    fun getName(): String {
        return Name_en
    }
    fun setName(name:String){
        this.Name_en = name
    }

    fun getAddress(): String {
        return Address_en
    }
    fun setAddress(address: String){
        this.Address_en = address
    }

    fun getCourtId(): String {
        return court_id
    }
    fun setCourtId(courtId: String){
        this.court_id = courtId
    }
    fun getopenhour(): String {
        return Opening_hours_en
    }

    fun getDistrict():String{
        return District_en
    }

    fun getFacility():String{
        return Ancillary_facilities_en
    }
    fun getCourtNo():String{
        return Court_no_en
    }
}