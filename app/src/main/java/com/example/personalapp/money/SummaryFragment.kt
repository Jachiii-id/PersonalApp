package com.example.personalapp.money

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalapp.R
import com.example.personalapp.data.Money
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class SummaryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var instrumenAdapter: InstrumenAdapter
    private lateinit var reportAdapter: ReportAdapter // Pastikan reportAdapter dideklarasikan
    private val instrumentList = mutableListOf<Money>()
    private val transactions = mutableListOf<Money>() // Tambahkan daftar transaksi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_summary, container, false)

        // Setup RecyclerView untuk laporan bulanan
        val reportRecyclerView: RecyclerView = view.findViewById(R.id.reportTransaction)
        reportRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reportAdapter = ReportAdapter(transactions) { selectedReport ->
            navigateToDetail("report", selectedReport)
        }
        reportRecyclerView.adapter = reportAdapter

        // Setup RecyclerView untuk instrumen
        recyclerView = view.findViewById(R.id.instrumenTransaction)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        instrumenAdapter = InstrumenAdapter(instrumentList) { selectedInstrument ->
            navigateToDetail("instrumen", selectedInstrument)
        }
        recyclerView.adapter = instrumenAdapter

        fetchInstrumen()
        fetchReport()

        return view
    }

    private fun navigateToDetail(filterType: String, money: Money) {
        val fragment = DetailSummaryFragment()
        val bundle = Bundle().apply {
            putString("filterType", filterType)
            putParcelable("moneyData", money) // Mengirimkan objek Money utuh
        }
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.summaryLayout, fragment)
            .addToBackStack(null)
            .commit()
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
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah ?: 0 }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah ?: 0 }
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

    private fun fetchReport() {
        val firestore = FirebaseFirestore.getInstance()

        Log.d("SummaryFragment", "Fetched ${transactions.size} transactions")

        firestore.collection("transactions")
            .get()
            .addOnSuccessListener { documents ->
                transactions.clear()

                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactions.add(transaction)
                }

                reportAdapter.updateData(transactions)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }


}
