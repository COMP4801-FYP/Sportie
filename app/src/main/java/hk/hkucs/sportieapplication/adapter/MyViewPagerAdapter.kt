package hk.hkucs.sportieapplication.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import hk.hkucs.sportieapplication.R
import hk.hkucs.sportieapplication.fragment.BookingStep1Fragment
import hk.hkucs.sportieapplication.fragment.BookingStep2Fragment
import hk.hkucs.sportieapplication.fragment.BookingStep3Fragment
import hk.hkucs.sportieapplication.fragment.BookingStep4Fragment

class MyViewPagerAdapter(fa:FragmentActivity) : FragmentStateAdapter(fa)  {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return BookingStep1Fragment()
            1 -> return BookingStep2Fragment()
            2 -> return BookingStep3Fragment()
            3 -> return BookingStep4Fragment()
        }
        return BookingStep1Fragment()
    }

}