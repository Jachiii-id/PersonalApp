package com.example.personalapp.money.summaryActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.personalapp.R
import com.example.personalapp.data.Money
import com.example.personalapp.money.otherMoney.InstrumenAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class DetailSummaryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var instrumenAdapter: InstrumenAdapter
    private val transactions = mutableListOf<Money>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_summary, container, false)

        recyclerView.adapter = instrumenAdapter

        val selectedInstrument = arguments?.getParcelable<Money>("moneyData")?.instrumen

        if (selectedInstrument != null) {
            fetchTransactionsByInstrument(selectedInstrument)
        }

        return view
    }

    private fun fetchTransactionsByInstrument(instrument: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions")
            .whereEqualTo("instrumen", instrument)
            .get()
            .addOnSuccessListener { documents ->
                transactions.clear()

                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactions.add(transaction)
                }

                val groupedData = transactions.groupBy { it.instrumen }
                    .mapValues { entry ->
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah ?: 0 }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah ?: 0 }
                        pemasukan - pengeluaran
                    }

                val totalSaldo = groupedData[instrument] ?: 0
                val formattedSaldo = NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalSaldo)

                view?.findViewById<TextView>(R.id.tv_nameInstrument)?.text = "Total: Rp $formattedSaldo"

                val resultList = groupedData.map { entry ->
                    Money(entry.key, "", entry.value, "", "")
                }

                instrumenAdapter.updateData(resultList)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
