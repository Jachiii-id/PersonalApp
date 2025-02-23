package com.example.personalapp.money.transactionActivity

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


class OutcomeTransaction : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mainTransactionAdapter: MainTransactionAdapter
    private val transactionList = mutableListOf<Money>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outcome_transaction, container, false)

        recyclerView = view.findViewById(R.id.outcomeTransaction)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mainTransactionAdapter = MainTransactionAdapter(transactionList)
        recyclerView.adapter = mainTransactionAdapter

        fetchTransactions()

        return view
    }

    private fun fetchTransactions() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("transactions")
            .whereEqualTo("jenis", "Pengeluaran")
            .get()
            .addOnSuccessListener { documents ->
                transactionList.clear()
                for (document in documents) {
                    val transaction = document.toObject(Money::class.java)
                    transactionList.add(transaction)
                }
                mainTransactionAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}