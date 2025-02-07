package com.example.personalapp.money

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.personalapp.MainActivity
import com.example.personalapp.R
import com.example.personalapp.databinding.ActivityInputMoneyBinding
import java.util.Calendar

class InputMoneyActivity : AppCompatActivity() {

    private lateinit var etDate: EditText
    private lateinit var binding: ActivityInputMoneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etDate = binding.etDate

        etDate.setOnClickListener {
            showDatePicker()
        }

        setupSpinners()

        val cancelTv = findViewById<TextView>(R.id.tv_cancel)

        cancelTv.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            etDate.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun setupSpinners() {
        val jenisList = listOf("Pemasukan", "Pengeluaran")
        val kategoriList = listOf("Fashion", "Makan & Minum", "Tabungan", "Kos + Peralatan", "Transport", "Lainnya")

        val jenisAdapter = ArrayAdapter(this, R.layout.spinner_item, jenisList)
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val kategoriAdapter = ArrayAdapter(this, R.layout.spinner_item, kategoriList)
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spnJenis.adapter = jenisAdapter
        binding.spnKategori.adapter = kategoriAdapter

    }
}
