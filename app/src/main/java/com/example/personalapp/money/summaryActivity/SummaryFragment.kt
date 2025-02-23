package com.example.personalapp.money.summaryActivity

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
import com.example.personalapp.money.otherMoney.InstrumenAdapter
import com.example.personalapp.money.otherMoney.ReportAdapter
import com.google.firebase.firestore.FirebaseFirestore

class SummaryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var instrumenAdapter: InstrumenAdapter
    private lateinit var reportAdapter: ReportAdapter
    private val instrumentList = mutableListOf<Money>()
    private val transactions = mutableListOf<Money>()

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
        val instrumentRecyclerView: RecyclerView = view.findViewById(R.id.instrumenTransaction)
        instrumentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        instrumenAdapter = InstrumenAdapter(instrumentList, onItemClick = { selectedInstrument ->
            navigateToDetail("instrument", selectedInstrument)
        }, isItemClickable = true)
        instrumentRecyclerView.adapter = instrumenAdapter

        fetchInstrumen()
        fetchReport()

        return view
    }

    private fun navigateToDetail(filterType: String, money: Money) {
        val fragment = DetailSummaryFragment()
        val bundle = Bundle().apply {
            putString("filterType", filterType)
            putParcelable("moneyData", money) // Mengirimkan objek Money yang telah diimplementasikan Parcelable
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
                // Initialize empty list
                instrumentList.clear()

                // Populate instrumentList with the fetched data
                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    instrumentList.add(transaction)
                }

                // Mengelompokkan transaksi berdasarkan instrumen
                val groupedData = instrumentList.groupBy { it.instrumen }
                    .mapValues { entry ->
                        // Calculate total amount (saldo) for each instrument
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah ?: 0 }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah ?: 0 }
                        pemasukan - pengeluaran // Final saldo
                    }
                    .map { Money(it.key, "", it.value, "", "") }

                // Update the adapter with grouped data
                instrumenAdapter.updateData(groupedData) // Pass grouped data to the adapter
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }


    private fun fetchReport() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions")
            .get()
            .addOnSuccessListener { documents ->
                transactions.clear()

                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactions.add(transaction)
                }

                reportAdapter.updateData(transactions) // Update data di adapter
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}

