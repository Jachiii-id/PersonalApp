package com.example.personalapp.money.otherMoney

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalapp.R
import com.example.personalapp.data.Money
import java.text.SimpleDateFormat
import java.util.*

class ReportAdapter(private val transactionList: List<Money>,
                    private val onItemClick: (Money) -> Unit) :
    RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    private var uniqueMonthsList: List<Money> = emptyList()  // List to hold unique month transactions

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMonths: TextView = itemView.findViewById(R.id.tv_months)

        fun bind(money: Money) {
            tvMonths.text = money.getFormattedDate()
            itemView.setOnClickListener { onItemClick(money) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_report, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = uniqueMonthsList.size  // Display size of unique months list

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(uniqueMonthsList[position])
    }

    // Fungsi untuk memperbarui data
    fun updateData(newList: List<Money>) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val monthsSet = linkedSetOf<String>() // Set to store unique month-year combinations
        val uniqueTransactions = mutableListOf<Money>()

        for (transaction in newList) {
            transaction.tanggal?.let {
                val formattedMonth = sdf.format(it.toDate())

                // Only add the transaction if the month has not been added yet
                if (formattedMonth !in monthsSet) {
                    monthsSet.add(formattedMonth)
                    uniqueTransactions.add(transaction)  // Add only the first transaction for each month
                }
            }
        }

        uniqueMonthsList = uniqueTransactions  // Update the list with unique transactions
        notifyDataSetChanged()  // Refresh RecyclerView
    }
}