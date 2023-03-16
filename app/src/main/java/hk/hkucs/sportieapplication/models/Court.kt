package hk.hkucs.sportieapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Court (
    private var name: String,
    private var address: String,
    private var courtId: String

):Parcelable {
    fun getName(): String {
        return name
    }
    fun setName(name:String){
        this.name = name
    }

    fun getAddress(): String {
        return address
    }
    fun setAddress(address: String){
        this.address = address
    }

    fun getCourtId(): String {
        return courtId
    }
    fun setCourtId(courtId: String){
        this.courtId = courtId
    }
}