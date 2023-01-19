package hk.hkucs.sportieapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User (
    val userid: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val gender: String = "",
    val profileCompleted: Int = 0
):Parcelable