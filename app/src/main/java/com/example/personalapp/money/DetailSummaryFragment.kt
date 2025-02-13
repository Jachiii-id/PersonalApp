package com.example.personalapp.money

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

        recyclerView = view.findViewById(R.id.DetailInstrumen)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        instrumenAdapter = InstrumenAdapter(transactions) { selectedTransaction ->
            // Bisa diarahkan ke fragment detail transaksi atau ditampilkan dalam log
            // Misalnya, jika ingin menampilkan detail transaksi:
            val fragment = DetailSummaryFragment()
            val bundle = Bundle().apply {
                putParcelable("moneyData", selectedTransaction)
            }
            fragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.summaryLayout, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = instrumenAdapter

        val selectedInstrument = arguments?.getParcelable<Money>("moneyData")?.instrumen

        if (selectedInstrument != null) {
            fetchTransactionsByInstrument(selectedInstrument)
        }

        recyclerView = view.findViewById(R.id.DetailInstrumen)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        instrumenAdapter = InstrumenAdapter(transactions) { selectedTransaction ->
            // Bisa diarahkan ke fragment detail transaksi atau ditampilkan dalam log
            val fragment = DetailSummaryFragment()
            val bundle = Bundle().apply {
                putParcelable("moneyData", selectedTransaction)
            }
            fragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.summaryLayout, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = instrumenAdapter

        return view
    }

    private fun fetchTransactionsByInstrument(instrument: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions")
            .whereEqualTo("instrumen", instrument) // Filter berdasarkan instrumen
            .get()
            .addOnSuccessListener { documents ->
                transactions.clear()

                // Mengambil data transaksi dan mengelompokkannya berdasarkan instrumen
                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactions.add(transaction)
                }

                // Mengelompokkan transaksi berdasarkan instrumen dan menghitung pemasukan dan pengeluaran
                val groupedData = transactions.groupBy { it.instrumen }
                    .mapValues { entry ->
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah ?: 0 }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah ?: 0 }
                        pemasukan - pengeluaran // Saldo akhir
                    }

                // Mengambil saldo untuk instrumen terpilih
                val totalSaldo = groupedData[instrument] ?: 0
                val formattedSaldo = NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalSaldo)

                // Menampilkan saldo total untuk instrumen terpilih
                view?.findViewById<TextView>(R.id.tv_nameInstrument)?.text = "Total: Rp $formattedSaldo"

                // Memperbarui RecyclerView dengan data yang sudah digrouping
                val resultList = groupedData.map { entry ->
                    Money(entry.key, "", entry.value, "", "") // Membuat objek Money baru dengan instrumen dan saldo akhir
                }

                // Memperbarui adapter dengan data yang sudah diproses
                instrumenAdapter.updateData(resultList)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }



}
