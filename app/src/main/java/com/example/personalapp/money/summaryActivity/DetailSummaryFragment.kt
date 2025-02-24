package com.example.personalapp.money.summaryActivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.personalapp.R
import com.example.personalapp.data.Money
import com.example.personalapp.money.otherMoney.InstrumenAdapter
import com.example.personalapp.money.transactionActivity.MainTransactionAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DetailSummaryFragment : Fragment() {

    private lateinit var instrumentAdapter: InstrumenAdapter
    private lateinit var transactionAdapter: MainTransactionAdapter
    private val transactionsList = mutableListOf<Money>()
    private val instrumentList = mutableListOf<Money>()
    private var selectedMonth: String? = null
    private var selectedInstrument: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_summary, container, false)

        // Retrieve the filter type and other arguments
        val filterType = arguments?.getString("filterType")
        selectedMonth = arguments?.getString("selectedMonth")
        selectedInstrument = arguments?.getString("selectedInstrument")

        // Set up RecyclerView for instrument
        val instrumentRecyclerView: RecyclerView = view.findViewById(R.id.DetailInstrumenPage)
        instrumentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        instrumentAdapter = InstrumenAdapter(instrumentList, onItemClick = { selectedInstrument ->
            // Handle instrument click
        })
        instrumentRecyclerView.adapter = instrumentAdapter

        val transactionRecyclerView: RecyclerView = view.findViewById(R.id.DetailTransactionPage)
        transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        transactionAdapter = MainTransactionAdapter(transactionsList)
        transactionRecyclerView.adapter = transactionAdapter

        when (filterType) {
            "instrument" -> {
                selectedInstrument?.let {
                    fetchTransactionsByInstrument(it)  // Filter by instrument
                }
            }
            "report" -> {
                selectedMonth?.let {
                    fetchTransactionsByMonth(it)  // Filter by month
                }
            }
        }

        return view
    }

    private fun fetchTransactionsByInstrument(instrument: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions")
            .whereEqualTo("instrumen", instrument)
            .get()
            .addOnSuccessListener { documents ->
                transactionsList.clear()
                instrumentList.clear()

                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactionsList.add(transaction)
                    instrumentList.add(transaction)
                }

                val groupedData = instrumentList.groupBy { it.instrumen }
                    .mapValues { entry ->
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah ?: 0 }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah ?: 0 }
                        pemasukan - pengeluaran // Final saldo
                    }
                    .map { Money(it.key, "", it.value, "", "") }

                Log.d("DetailSummaryFragment", "Transactions for Instruments: ${transactionsList.size}")

                // Update the adapter with the filtered data
                transactionAdapter.notifyDataSetChanged()
                instrumentAdapter.updateData(groupedData)

                view?.let {
                    fetchAmount(it, transactionsList)  // Hanya dipanggil jika view tidak null
                }            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun fetchTransactionsByMonth(month: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions")
            .get()
            .addOnSuccessListener { documents ->
                transactionsList.clear()
                instrumentList.clear()

                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transaction.tanggal?.let {
                        val formattedMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(it.toDate())
                        if (formattedMonth == month) {
                            transactionsList.add(transaction)
                            instrumentList.add(transaction)
                        }
                    }
                }

                val groupedData = instrumentList.groupBy { it.instrumen }
                    .mapValues { entry ->
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah ?: 0 }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah ?: 0 }
                        pemasukan - pengeluaran // Final saldo
                    }
                    .map { Money(it.key, "", it.value, "", "") }

                Log.d("DetailSummaryFragment", "Transactions for Instruments: ${transactionsList.size}")

                // Update the adapter with the filtered data
                transactionAdapter.notifyDataSetChanged()
                instrumentAdapter.updateData(groupedData)

                view?.let {
                    fetchAmount(it, transactionsList)  // Hanya dipanggil jika view tidak null
                }            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }


    private fun fetchAmount(view: View, filteredTransactions: List<Money>) {
        // Filter transaksi yang sudah difilter berdasarkan instrumen atau bulan
        val groupedData = filteredTransactions.groupBy { it.instrumen }
            .mapValues { entry ->
                val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                pemasukan - pengeluaran
            }

        val totalSaldo = groupedData.values.sum()
        val formattedSaldo = NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalSaldo)

        val income = filteredTransactions.groupBy { it.instrumen }
            .mapValues { entry ->
                val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                pemasukan
            }

        val outcome = filteredTransactions.groupBy { it.instrumen }
            .mapValues { entry ->
                val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                pengeluaran
            }

        val amount = view.findViewById<TextView>(R.id.tv_amountDetailSummary)
        amount?.text = "Rp $formattedSaldo"

        val incomeTotal = income.values.sum()
        val incomeSaldo = NumberFormat.getNumberInstance(Locale("id", "ID")).format(incomeTotal)

        val incomeRecent = view.findViewById<TextView>(R.id.tv_incomeDetailRecent)
        incomeRecent?.text = "Rp $incomeSaldo"

        val outcomeTotal = outcome.values.sum()
        val outcomeSaldo = NumberFormat.getNumberInstance(Locale("id", "ID")).format(outcomeTotal)

        val outcomeRecent = view.findViewById<TextView>(R.id.tv_outcomeDetailRecent)
        outcomeRecent?.text = "Rp $outcomeSaldo"
    }

}
