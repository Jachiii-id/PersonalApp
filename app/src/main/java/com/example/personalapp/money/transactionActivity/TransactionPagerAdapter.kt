package com.example.personalapp.money.transactionActivity

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TransactionPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> IncomeTransaction()
            1 -> OutcomeTransaction()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}