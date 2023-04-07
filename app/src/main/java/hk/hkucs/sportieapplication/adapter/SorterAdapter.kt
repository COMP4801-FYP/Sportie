package hk.hkucs.sportieapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.models.Sorter

class SorterAdapter(private val context: Context, private val sorterlist: List<Sorter>) : BaseAdapter() {

    override fun getCount(): Int {
        return sorterlist.size
    }

    override fun getItem(position: Int): Any {
        return sorterlist[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = convertView ?: LayoutInflater.from(context).inflate(R.layout.sort_item, parent, false)

        val txtName = rootView.findViewById<TextView>(R.id.name)
        val image = rootView.findViewById<ImageView>(R.id.image)

        txtName.text = sorterlist[position].getName()
        image.setImageResource(sorterlist[position].getImage())

        return rootView
    }
}
