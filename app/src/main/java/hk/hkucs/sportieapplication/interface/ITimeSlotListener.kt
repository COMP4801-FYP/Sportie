package hk.hkucs.sportieapplication.`interface`

import hk.hkucs.sportieapplication.models.TimeSlot

interface ITimeSlotListener {
    fun onTimeSlotLoadSuccess(timeSlotList:ArrayList<TimeSlot>)
    fun onTimeSlotLoadFailed(message:String)
    fun onTimeSlotLoadEmpty()
}