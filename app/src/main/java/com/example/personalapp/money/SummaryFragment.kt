package com.example.personalapp.money

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalapp.R
import com.example.personalapp.data.Money
import com.google.firebase.firestore.FirebaseFirestore

class SummaryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var instrumenAdapter: InstrumenAdapter
    private val instrumentList = mutableListOf<Money>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_summary, container, false)

        recyclerView = view.findViewById(R.id.instrumenTransaction)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        instrumenAdapter = InstrumenAdapter(instrumentList)
        recyclerView.adapter = instrumenAdapter

        fetchInstrumen()

        return view
    }

    private fun fetchInstrumen() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions")
            .get()
            .addOnSuccessListener { documents ->
                val transactions = mutableListOf<Money>()

                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactions.add(transaction)
                }

                // Mengelompokkan transaksi berdasarkan instrumen
                val groupedData = transactions.groupBy { it.instrumen }
                    .mapValues { entry ->
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah }
                        pemasukan - pengeluaran // Saldo akhir
                    }
                    .map { Money(it.key, "", it.value, "", "") }

                // Memperbarui daftar yang ditampilkan di RecyclerView
                instrumentList.clear()
                instrumentList.addAll(groupedData)
                instrumenAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

}