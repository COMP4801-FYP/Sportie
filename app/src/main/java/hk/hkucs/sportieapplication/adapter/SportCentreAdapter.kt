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
import hk.hkucs.sportieapplication.Common.Common.Companion.ALL_DISTRICT
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.`interface`.IRecyclerItemSelectedListener
import hk.hkucs.sportieapplication.models.SportCentre

class SportCentreAdapter(requireActivity: Context, sportCentreArray: ArrayList<SportCentre>) : RecyclerView.Adapter<SportCentreAdapter.MyViewHolder>() {
    var courtList: ArrayList<SportCentre> = sportCentreArray
    var cardViewList: ArrayList<CardView> = ArrayList()
    var context:Context = requireActivity
    var localBroadcastManager = LocalBroadcastManager.getInstance(context)

    inner class MyViewHolder: RecyclerView.ViewHolder{
        var txtCourtName: TextView
        var txtCourtAddress: TextView
        var card_court: CardView
        lateinit var iRecyclerItemSelectedListener: IRecyclerItemSelectedListener

        fun setiRecyclerItemSelectedListener(iRecyclerItemSelectedListener: IRecyclerItemSelectedListener){
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener
        }

        constructor(itemView:View) : super(itemView){
            txtCourtName = itemView.findViewById(R.id.txt_court_name)
            txtCourtAddress = itemView.findViewById(R.id.txt_court_address)
            card_court = itemView.findViewById(R.id.card_court)

            itemView.setOnClickListener(){
                iRecyclerItemSelectedListener.onItemSelectedListener(itemView, absoluteAdapterPosition)
            }
        }

        fun onClick(v: View?) {
            if (v != null) {
                iRecyclerItemSelectedListener.onItemSelectedListener(v, bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.layout_court, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtCourtName.text = courtList[position].getName()
        holder.txtCourtAddress.text = courtList[position].getAddress()
        if (!cardViewList.contains(holder.card_court)){
            cardViewList.add(holder.card_court)
        }

        holder.setiRecyclerItemSelectedListener(object : IRecyclerItemSelectedListener {
            override fun onItemSelectedListener(view: View, pos: Int) {

                // set white background for all card not be selected
                for(cardview in cardViewList){
                    cardview.setCardBackgroundColor(context.getColor(R.color.white))
                }
                // set selected background for only selected item
//                holder.card_court.setCardBackgroundColor(context.getColor(R.color.holo_orange_dark))
                holder.card_court.setCardBackgroundColor(context.getColor(R.color.green2))

                // if no district selected, save the district chosen to global variable
                if (Common.district == ALL_DISTRICT){
                    Common.district = courtList[pos].District_en
                }

                // send broadcast to tell Booking Activity enable button next
                val intent = Intent("ENABLE_BUTTON_NEXT")
                intent.putExtra("COURT_SAVE", courtList[pos])
                intent.putExtra("STEP", 1)
                localBroadcastManager.sendBroadcast(intent)
            }
        })
    }

    override fun getItemCount(): Int {
        return courtList.size
    }

//    fun setFilteredList(filteredList:ArrayList<SportCentre>){
//        this.courtList = filteredList
//        notifyDataSetChanged()
//    }
}