package com.example.personalapp.money

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalapp.R
import com.example.personalapp.data.Money
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class IncomeTransactionAdapter(private val transactionList: List<Money>) :
    RecyclerView.Adapter<IncomeTransactionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIncome: TextView = itemView.findViewById(R.id.tv_incomePay)
        val tvDetail: TextView = itemView.findViewById(R.id.tv_detailPay)
        val tvInstrumen: TextView = itemView.findViewById(R.id.tv_instrumenPay)
        val tvTanggal: TextView = itemView.findViewById(R.id.tv_tanggalPay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_income, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList[position]

        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedAmount = formatter.format(transaction.jumlah)
        holder.tvIncome.text = "Rp. $formattedAmount"
        holder.tvDetail.text = transaction.notes
        holder.tvInstrumen.text = transaction.instrumen

        // Konversi Timestamp ke String Tanggal
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.tvTanggal.text = transaction.tanggal?.let { sdf.format(it.toDate()) } ?: "No Date"
    }

    override fun getItemCount(): Int = transactionList.size
}
