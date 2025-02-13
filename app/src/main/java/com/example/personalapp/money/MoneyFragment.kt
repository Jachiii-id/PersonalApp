package com.example.personalapp.money

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.personalapp.R
import com.example.personalapp.data.Money
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

class MoneyFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_money, container, false)

        tabLayout = view.findViewById(R.id.tl_transaction)
        viewPager = view.findViewById(R.id.vp_transactions)

        // Set Adapter ke ViewPager2
        viewPager.adapter = TransactionPagerAdapter(this)

        // Hubungkan TabLayout dengan ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Income"
                1 -> "Outcome"
                else -> "Unknown"
            }
        }.attach()

        val cardView: View = view.findViewById(R.id.cv_cardInformation)
        cardView.setOnClickListener {
            val intent = Intent(requireContext(), SummaryActivity::class.java)
            startActivity(intent)
        }

        fetchAmount(view)

        return view
    }

    private fun fetchAmount(view: View) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions") // Pastikan collection nama benar
            .get()
            .addOnSuccessListener { documents ->
                val transactions = mutableListOf<Money>()

                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactions.add(transaction)
                }

                // Mengelompokkan transaksi berdasarkan instrumen dan menghitung saldo
                val groupedData = transactions.groupBy { it.instrumen }
                    .mapValues { entry ->
                        // Filter berdasarkan jenis dan konversi jumlah ke Double
                        val pemasukan = entry.value.filter { it.jenis == "Pemasukan" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                        val pengeluaran = entry.value.filter { it.jenis == "Pengeluaran" }.sumOf { it.jumlah?.toDouble() ?: 0.0 }
                        pemasukan - pengeluaran // Saldo akhir
                    }

                // Menampilkan total saldo di TextView dengan format "Rp. 200.000"
                val totalSaldo = groupedData.values.sum() // Jumlahkan saldo dari setiap instrumen
                val formattedSaldo = NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalSaldo)

                val amount = view.findViewById<TextView>(R.id.tv_amountMoney)
                amount?.text = "Rp $formattedSaldo"
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }


}
