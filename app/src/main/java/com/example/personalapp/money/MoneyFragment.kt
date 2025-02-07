package com.example.personalapp.money

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.personalapp.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MoneyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_money, container, false)

        val fragmentList = arrayListOf(
            IncomeTransaction(),
            OutcomeTransaction()
        )

        val adapter = TransactionAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = view.findViewById<ViewPager2>(R.id.vp_transactions)
        val tabLayout = view.findViewById<TabLayout>(R.id.tl_transaction)

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Income" else "Outcome"
        }.attach()

        // Memastikan indikator aktif diterapkan dengan benar
        tabLayout.setSelectedTabIndicator(R.drawable.tab_indicator)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                println("Tab ${tab.text} selected")
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                println("Tab ${tab.text} unselected")
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                println("Tab ${tab.text} reselected")
            }
        })

        return view
    }
}
