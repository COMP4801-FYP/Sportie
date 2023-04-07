package hk.hkucs.sportieapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Sorter (
    private var name: String,
    private var image: Int,



):Parcelable {
    fun getName(): String {
        return name
    }


    fun getImage(): Int {
        return image
    }


}