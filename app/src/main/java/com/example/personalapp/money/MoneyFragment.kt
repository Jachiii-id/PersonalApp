package com.example.personalapp.money

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.personalapp.R
import com.google.android.material.tabs.TabLayout

class MoneyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_money, container, false)

//        val fragmentList = arrayListOf<Fragment>(
//            IncomeTransaction(),
//            OutcomeTransaction()
//        )
//
//        val adapter = TransactionAdapter(
//            fragmentList,
//            requireActivity().supportFragmentManager,
//            lifecycle
//        )
//
//        val viewPager = view.findViewById<ViewPager2>(R.id.vp_transactions)
//
//        viewPager.adapter = adapter
//        val tab = view.findViewById<TabLayout>(R.id.tl_transaction)
//
//        tab.isAttachedToWindow(viewPager)

        val tabLayout = view.findViewById<TabLayout>(R.id.tl_transaction)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.active_tab_background)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab.view.setBackgroundResource(android.R.color.transparent)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

}