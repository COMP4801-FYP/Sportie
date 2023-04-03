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
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.`interface`.IRecyclerItemSelectedListener
import hk.hkucs.sportieapplication.models.Court

class CourtAdapter(requireActivity: Context, courtArray: ArrayList<Court>) : RecyclerView.Adapter<CourtAdapter.MyViewHolder>() {
    var context:Context = requireActivity
    var courtList: ArrayList<Court> = courtArray
    var cardViewList: ArrayList<CardView> = ArrayList()
    var localBroadcastManager = LocalBroadcastManager.getInstance(context)

    inner class MyViewHolder: RecyclerView.ViewHolder {
        var txtCourtName: TextView
        var card_court: CardView
        lateinit var iRecyclerItemSelectedListener: IRecyclerItemSelectedListener

        fun setiRecyclerItemSelectedListener(iRecyclerItemSelectedListener: IRecyclerItemSelectedListener){
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener
        }

        constructor(itemView: View) : super(itemView){
            txtCourtName = itemView.findViewById(R.id.txt_court_name)
            card_court = itemView.findViewById(R.id.card_court)

            itemView.setOnClickListener(){
                iRecyclerItemSelectedListener.onItemSelectedListener(itemView, absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtAdapter.MyViewHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.layout_court_no, parent, false)
        return MyViewHolder(itemView)
    }


    override fun getItemCount(): Int {
        return courtList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtCourtName.text = courtList[position].getName()
        if(!cardViewList.contains(holder.card_court)){
            cardViewList.add(holder.card_court)
        }

        holder.setiRecyclerItemSelectedListener(object : IRecyclerItemSelectedListener {
            override fun onItemSelectedListener(view: View, pos: Int) {
                // set white background for all card not be selected
                for(cardview in cardViewList){
                    cardview.setCardBackgroundColor(context.getColor(R.color.white))
                }

                // set background for choice
                holder.card_court.setCardBackgroundColor(context.getColor(R.color.themeYellow))

                // send broadcast to tell Booking Activity enable button next
                val intent = Intent("ENABLE_BUTTON_NEXT")
                intent.putExtra("COURT_SELECTED", courtList[pos])
                intent.putExtra("STEP", 2)
                localBroadcastManager.sendBroadcast(intent)
            }
        })
    }

}