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

class IncomeTransaction : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var incomeTransactionAdapter: IncomeTransactionAdapter
    private val transactionList = mutableListOf<Money>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_income_transaction, container, false)

        recyclerView = view.findViewById(R.id.incomeTransaction)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        incomeTransactionAdapter = IncomeTransactionAdapter(transactionList)
        recyclerView.adapter = incomeTransactionAdapter

        fetchTransactions()

        return view
    }

    private fun fetchTransactions() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions")
            .whereEqualTo("jenis", "Pemasukan")
            .get()
            .addOnSuccessListener { documents ->
                transactionList.clear()
                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactionList.add(transaction)
                }
                incomeTransactionAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

}