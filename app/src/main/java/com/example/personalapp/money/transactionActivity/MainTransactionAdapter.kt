package com.example.personalapp.money.transactionActivity

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

class MainTransactionAdapter(
    private val transactionList: List<Money>
) : RecyclerView.Adapter<MainTransactionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, val tvAmount: TextView, val tvDetail: TextView, val tvInstrumen: TextView, val tvTanggal: TextView) :
        RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        view = if (viewType == R.layout.rv_income) {
            LayoutInflater.from(parent.context).inflate(R.layout.rv_income, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.rv_outcome, parent, false)
        }

        // Pastikan ID ditemukan dengan benar
        val tvAmount: TextView = view.findViewById(R.id.tv_textPay)
        val tvDetail: TextView = view.findViewById(R.id.tv_detailPay)
        val tvInstrumen: TextView = view.findViewById(R.id.tv_instrumenPay)
        val tvTanggal: TextView = view.findViewById(R.id.tv_tanggalPay)

        return ViewHolder(view, tvAmount, tvDetail, tvInstrumen, tvTanggal)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList[position]

        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedAmount = formatter.format(transaction.jumlah)
        holder.tvAmount.text = "Rp. $formattedAmount"
        holder.tvDetail.text = transaction.notes
        holder.tvInstrumen.text = transaction.instrumen

        // Konversi Timestamp ke String Tanggal
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.tvTanggal.text = transaction.tanggal?.let { sdf.format(it.toDate()) } ?: "No Date"
    }

    override fun getItemCount(): Int = transactionList.size

    override fun getItemViewType(position: Int): Int {
        val transaction = transactionList[position]

        return if (transaction.jenis == "Pemasukan") {
            R.layout.rv_income
        } else {
            R.layout.rv_outcome
        }
    }
}
