package com.example.personalapp.money

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalapp.R
import com.example.personalapp.data.Money
import java.text.NumberFormat
import java.util.Locale

class InstrumenAdapter(
    private var instrumentList: List<Money>,
    private val onItemClick: (Money) -> Unit
) : RecyclerView.Adapter<InstrumenAdapter.ViewHolder>() {

    fun updateData(newData: List<Money>) {
        instrumentList = newData
        notifyDataSetChanged() // Memberitahukan RecyclerView untuk memperbarui data
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNameInstrumen: TextView = itemView.findViewById(R.id.tv_nameInstrument)
        private val tvPayInstrument: TextView = itemView.findViewById(R.id.tv_instrumenPay)

        fun bind(money: Money) {
            tvNameInstrumen.text = money.instrumen
            tvPayInstrument.text = formatRupiah(money.jumlah ?: 0)

            itemView.setOnClickListener {
                onItemClick(money)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_instrument, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val money = instrumentList[position]
        holder.bind(money)
    }

    override fun getItemCount(): Int = instrumentList.size

    private fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp. ${formatter.format(amount)}"
    }
}
