package com.example.personalapp.money.summaryActivity

import android.os.Bundle
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
import com.example.personalapp.money.otherMoney.ReportAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class SummaryFragment : Fragment() {

    private lateinit var instrumenAdapter: InstrumenAdapter
    private lateinit var reportAdapter: ReportAdapter
    private val instrumentList = mutableListOf<Money>()
    private val transactions = mutableListOf<Money>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_summary, container, false)

        val reportRecyclerView: RecyclerView = view.findViewById(R.id.reportTransaction)
        reportRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        reportAdapter = ReportAdapter(transactions) { selectedReport ->
            navigateToDetail("report", selectedReport)
        }
        reportRecyclerView.adapter = reportAdapter

        // Setup RecyclerView for instruments
        val instrumentRecyclerView: RecyclerView = view.findViewById(R.id.instrumenTransaction)
        instrumentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        instrumenAdapter = InstrumenAdapter(instrumentList, onItemClick = { selectedInstrument ->
            navigateToDetail("instrument", selectedInstrument)
        }, isItemClickable = true)
        instrumentRecyclerView.adapter = instrumenAdapter

        fetchInstrumen()
        fetchReport()
        fetchAmount(view)

        return view
    }

    // Function to navigate to DetailSummaryFragment
    private fun navigateToDetail(filterType: String, money: Money) {
        val fragment = DetailSummaryFragment()
        val bundle = Bundle().apply {
            putString("filterType", filterType)  // Pass the filter type
            putParcelable("moneyData", money)    // Pass selected money data

            // Pass only the relevant filter data based on the filter type
            if (filterType == "instrument") {
                putString("selectedInstrument", money.instrumen) // Only instrument
            } else if (filterType == "report") {
                putString("selectedMonth", money.getFormattedDate())  // Only month
            }
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

                // Group transactions by instrument and calculate saldo
                val groupedData = instrumentList.groupBy { it.instrumen }
                    .mapValues { entry ->
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah ?: 0 }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah ?: 0 }
                        pemasukan - pengeluaran // Final saldo
                    }
                    .map { Money(it.key, "", it.value, "", "") }

                // Update the adapter with grouped data
                instrumenAdapter.updateData(groupedData)
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

                reportAdapter.updateData(transactions)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun fetchAmount(view: View) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions")
            .get()
            .addOnSuccessListener { documents ->
                val transactions = mutableListOf<Money>()

                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactions.add(transaction)
                }

                val groupedData = transactions.groupBy { it.instrumen }
                    .mapValues { entry ->
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                        pemasukan - pengeluaran
                    }

                val totalSaldo = groupedData.values.sum()
                val formattedSaldo = NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalSaldo)

                val income = transactions.groupBy { it.instrumen }
                    .mapValues { entry ->
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                        pemasukan
                    }

                val outcome = transactions.groupBy { it.instrumen }
                    .mapValues { entry ->
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                        pengeluaran
                    }

                val amount = view.findViewById<TextView>(R.id.tv_amountSummary)
                amount?.text = "Rp $formattedSaldo"

                val incomeTotal = income.values.sum()
                val incomeSaldo = NumberFormat.getNumberInstance(Locale("id", "ID")).format(incomeTotal)

                val incomeRecent = view.findViewById<TextView>(R.id.tv_incomeRecent)
                incomeRecent?.text = "Rp $incomeSaldo"

                val outcomeTotal = outcome.values.sum()
                val outcomeSaldo = NumberFormat.getNumberInstance(Locale("id", "ID")).format(outcomeTotal)

                val outcomeRecent = view.findViewById<TextView>(R.id.tv_outcomeRecent)
                outcomeRecent?.text = "Rp $outcomeSaldo"

            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}


