package com.example.personalapp.money.otherMoney

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personalapp.R
import com.example.personalapp.data.Money
import java.text.NumberFormat
import java.util.*

class InstrumenAdapter(
    private var instrumenLis: List<Money>,
    private val onItemClick: (Money) -> Unit,
    private var isItemClickable: Boolean = true
) : RecyclerView.Adapter<InstrumenAdapter.ViewHolder>() {

    private var groupedInstrumentList: List<Money> = emptyList()

    fun updateData(newData: List<Money>) {
        val groupedData = newData.groupBy { it.instrumen }
            .map { entry ->
                val totalAmount = entry.value.sumOf { it.jumlah ?: 0 }
                Money(instrumen = entry.key, jumlah = totalAmount)
            }

        groupedInstrumentList = groupedData
        notifyDataSetChanged()
    }

    fun setItemClickable(clickable: Boolean) {
        isItemClickable = clickable
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNameInstrumen: TextView = itemView.findViewById(R.id.tv_nameInstrument)
        private val tvPayInstrument: TextView = itemView.findViewById(R.id.tv_amountInstrumentPay)

        fun bind(money: Money) {
            tvNameInstrumen.text = money.instrumen
            tvPayInstrument.text = formatRupiah(money.jumlah ?: 0)

            // Handle item clickability
            if (isItemClickable) {
                itemView.setOnClickListener {
                    onItemClick(money)
                }
            } else {
                itemView.setOnClickListener(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_instrument, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val money = groupedInstrumentList[position]
        holder.bind(money)
    }

    override fun getItemCount(): Int = groupedInstrumentList.size

    private fun formatRupiah(amount: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp. ${formatter.format(amount)}"
    }
}
