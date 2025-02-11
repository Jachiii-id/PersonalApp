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

class InstrumenAdapter(private val instrumentList: List<Money>) :
    RecyclerView.Adapter<InstrumenAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameInstrumen: TextView = itemView.findViewById(R.id.tv_nameInstrument)
        val tvPayInstrument: TextView = itemView.findViewById(R.id.tv_instrumenPay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_instrument, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instrument = instrumentList[position]

        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedAmount = formatter.format(instrument.jumlah)

        holder.tvPayInstrument.text = "Rp. $formattedAmount"
        holder.tvNameInstrumen.text = instrument.instrumen
    }

    override fun getItemCount(): Int = instrumentList.size
}
