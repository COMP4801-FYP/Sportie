package hk.hkucs.sportieapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.`interface`.IRecyclerItemSelectedListener
import hk.hkucs.sportieapplication.models.Court
import hk.hkucs.sportieapplication.models.TimeSlot

class TimeSlotAdapter(requireActivity: Context, timeSlotArray: ArrayList<TimeSlot>) : RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder>() {
    constructor(requireActivity: Context) : this(requireActivity, ArrayList())

    var context:Context = requireActivity
    var timeSlotList: ArrayList<TimeSlot> = timeSlotArray

    inner class MyViewHolder: RecyclerView.ViewHolder {
        var txt_time_slot: TextView
        var txt_time_slot_description: TextView
        var card_time_slot: CardView
//        lateinit var iRecyclerItemSelectedListener: IRecyclerItemSelectedListener

//        fun setiRecyclerItemSelectedListener(iRecyclerItemSelectedListener: IRecyclerItemSelectedListener){
//            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener
//        }

        constructor(itemView: View) : super(itemView){
            txt_time_slot = itemView.findViewById(R.id.txt_time_slot)
            txt_time_slot_description = itemView.findViewById(R.id.txt_time_slot_description)
            card_time_slot = itemView.findViewById(R.id.card_time_slot)

//            itemView.setOnClickListener(){
//                iRecyclerItemSelectedListener.onItemSelectedListener(itemView, absoluteAdapterPosition)
//            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.layout_time_slot, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txt_time_slot.text = java.lang.StringBuilder(Common.convertTimeSlotToString(position))

        // if all position is available, just show list
        if(timeSlotList.size == 0) {
            holder.txt_time_slot_description.text = "Available"
            holder.txt_time_slot_description.setTextColor(context.getColor(R.color.black))
            holder.txt_time_slot.setTextColor(context.getColor(R.color.black))
            holder.card_time_slot.setCardBackgroundColor(context.getColor(R.color.white))
        }
        // if have position is booked
        else{
            for(slotValue in timeSlotList){
                // loop all time slot from server and set different color
                var slot = Integer.parseInt(slotValue.getSlot().toString())
                if(slot == position){
                    holder.card_time_slot.setCardBackgroundColor(context.getColor(R.color.darker_gray))
                    holder.txt_time_slot_description.text = "Full"
                    holder.txt_time_slot_description.setTextColor(context.getColor(R.color.white))
                    holder.txt_time_slot.setTextColor(context.getColor(R.color.white))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return Common.TIME_SLOT_TOTAL
    }

}