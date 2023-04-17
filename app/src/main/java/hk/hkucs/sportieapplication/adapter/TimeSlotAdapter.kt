package hk.hkucs.sportieapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import hk.hkucs.sportieapplication.Common.Common
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.`interface`.IRecyclerItemSelectedListener
import hk.hkucs.sportieapplication.models.TimeSlot
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TimeSlotAdapter(requireActivity: Context, timeSlotArray: ArrayList<TimeSlot>) : RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder>() {
    constructor(requireActivity: Context) : this(requireActivity, ArrayList())

    var context:Context = requireActivity
    var timeSlotList: ArrayList<TimeSlot> = timeSlotArray
    private var lastSelectedCardView: CardView? = null

    private lateinit var cardViewList: ArrayList<CardView>
    private lateinit var localBroadcastManager: LocalBroadcastManager


    inner class MyViewHolder: RecyclerView.ViewHolder {
        // Keep track of the previously selected card view
        var txt_time_slot: TextView
        var txt_time_slot_description: TextView
        var card_time_slot: CardView
        lateinit var iRecyclerItemSelectedListener: IRecyclerItemSelectedListener

        fun setiRecyclerItemSelectedListener(iRecyclerItemSelectedListener: IRecyclerItemSelectedListener){
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener
        }

        constructor(itemView: View) : super(itemView){
            txt_time_slot = itemView.findViewById(R.id.txt_time_slot)
            txt_time_slot_description = itemView.findViewById(R.id.txt_time_slot_description)
            card_time_slot = itemView.findViewById(R.id.card_time_slot)
            localBroadcastManager = LocalBroadcastManager.getInstance(context)
            cardViewList = ArrayList()

            itemView.setOnClickListener(){
                iRecyclerItemSelectedListener.onItemSelectedListener(itemView, absoluteAdapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.layout_time_slot, parent, false)
        return MyViewHolder(itemView)
    }

    @Suppress("RecyclerView")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // slot time
        var slottime = java.lang.StringBuilder(Common.convertTimeSlotToString(position))
        holder.txt_time_slot.text = slottime

        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        var currentdate = currentTime.get(Calendar.DATE)
        var simpleDateFromat = SimpleDateFormat("dd_MM_yyyy")
        var chosendate = simpleDateFromat.format(Common.bookingDate.time).substring(0,2).toInt()
        println("curdate ${currentdate}, bookdate ${chosendate}" )

        var slothour: Int
        if (slottime[1] == ':') {
            slothour = slottime.substring(0, 1).toInt()
        } else {
            slothour = slottime.substring(0, 2).toInt()
        }
        println("hour ${currentHour}")
        println("slottime ${slothour}")

        // if all position is available, just show list
        if(timeSlotList.size == 0) {
            if (slothour < currentHour && currentdate == chosendate) { // if current time is later than booking slot time

                holder.card_time_slot.isEnabled = false
                holder.card_time_slot.setCardBackgroundColor(context.getColor(R.color.darker_gray))
                holder.txt_time_slot_description.text = "Time passed"
                holder.txt_time_slot_description.setTextColor(context.getColor(R.color.white))
                holder.txt_time_slot.setTextColor(context.getColor(R.color.white))
            }
            else{
                // if all time slot is empty, all card is enable
                holder.card_time_slot.isEnabled = true

                holder.txt_time_slot_description.text = "Available"
                holder.txt_time_slot_description.setTextColor(context.getColor(R.color.black))
                holder.txt_time_slot.setTextColor(context.getColor(R.color.black))
                holder.card_time_slot.setCardBackgroundColor(context.getColor(R.color.white))
            }
        }
        // if have position booked
        else{
            if (slothour < currentHour && currentdate == chosendate) { // if current time is later than booking slot time

                holder.card_time_slot.isEnabled = false
                holder.card_time_slot.setCardBackgroundColor(context.getColor(R.color.darker_gray))
                holder.txt_time_slot_description.text = "Time passed"
                holder.txt_time_slot_description.setTextColor(context.getColor(R.color.white))
                holder.txt_time_slot.setTextColor(context.getColor(R.color.white))
            } else {

                for(slotValue in timeSlotList){
                    // loop all time slot from server and set different color
                    var slot = Integer.parseInt(slotValue.getSlot().toString())
                    if(slot == position){

                        // set tag for all time slot is full
                        holder.card_time_slot.tag = "DISABLE"

                        holder.card_time_slot.isEnabled = false

                        holder.card_time_slot.setCardBackgroundColor(context.getColor(R.color.darker_gray))
                        holder.txt_time_slot_description.text = "Booked"
                        holder.txt_time_slot_description.setTextColor(context.getColor(R.color.white))
                        holder.txt_time_slot.setTextColor(context.getColor(R.color.white))
                    }
                }
            }
        }

        // Add all card to list
        if (!cardViewList.contains(holder.card_time_slot)){
            cardViewList.add(holder.card_time_slot)
        }

        var timeslotpos = TimeSlot()
        timeslotpos.setSlot(position.toLong())
        if (!timeSlotList.contains(timeslotpos)){
//             check if card time_slot is available
            holder.setiRecyclerItemSelectedListener(object : IRecyclerItemSelectedListener {
                override fun onItemSelectedListener(view: View, pos: Int) {
                    // loop all card in card list
                    for (cardview in cardViewList){

                        // only available card time slot will be changed
                        if(cardview.tag == null){
                            cardview.setCardBackgroundColor(context.resources.getColor(android.R.color.white))
                        }
//                        cardview.setCardBackgroundColor(context.resources.getColor(android.R.color.white))
                    }
                    // Reset the color of the previously selected card view, if any
                    lastSelectedCardView?.setCardBackgroundColor(context.resources.getColor(android.R.color.white))

                    // selected card will change color
                    holder.card_time_slot.setCardBackgroundColor(context.resources.getColor(R.color.themeYellow))

                    // then send broadcast to enable NEXT button
                    val intent = Intent("ENABLE_BUTTON_NEXT")
                    intent.putExtra("TIME_SLOT", position)
                    intent.putExtra("STEP", 3)
                    localBroadcastManager.sendBroadcast(intent)

                    // Update the last selected card view
                    lastSelectedCardView = holder.card_time_slot
                }
            })
        }
    }

    override fun getItemCount(): Int {
//        return Common.TIME_SLOT_TOTAL
        if (Common.currentSportCentre!!.getopenhour() == " - "){
            return 12
        }

        if ((Common.currentSportCentre!!.getopenhour().split(" - ")[0].slice(3..4)) == "00"){
            return Common.currentSportCentre!!.getopenhour().split(" - ")[1].slice(0..1).toInt() - Common.currentSportCentre!!.getopenhour().split(" - ")[0].slice(0..1).toInt()
        }
        else{
            return Common.currentSportCentre!!.getopenhour().split(" - ")[1].slice(0..1).toInt() - Common.currentSportCentre!!.getopenhour().split(" - ")[0].slice(0..1).toInt()-1
        }

    }

    // to prevent unstable selected item position
    override fun getItemViewType(position: Int): Int {
        return position
    }


}