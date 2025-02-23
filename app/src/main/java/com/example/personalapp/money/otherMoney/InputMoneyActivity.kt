package com.example.personalapp.money.otherMoney

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.personalapp.MainActivity
import com.example.personalapp.R
import com.example.personalapp.databinding.ActivityInputMoneyBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InputMoneyActivity : AppCompatActivity() {

    private lateinit var etDate: EditText
    private lateinit var binding: ActivityInputMoneyBinding
    private val firestore: FirebaseFirestore by lazy{ FirebaseFirestore.getInstance() }

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

        binding.btnSimpan.setOnClickListener{
            saveToFirestore()
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

    private fun saveToFirestore() {
        val dateString = binding.etDate.text.toString()
        val amountString = binding.etJumlah.text.toString()
        val type = binding.spnJenis.selectedItem.toString()
        val category = binding.spnKategori.selectedItem.toString()
        val instrument = binding.etInstrumen.text.toString()
        val notes = binding.etNotes.text.toString()

        if (dateString.isEmpty() || amountString.isEmpty() || instrument.isEmpty()) {
            Toast.makeText(this, "Harap isi semua data!", Toast.LENGTH_SHORT).show()
            return
        }

        // Konversi jumlah ke Integer
        val amount = try {
            amountString.toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Jumlah harus berupa angka!", Toast.LENGTH_SHORT).show()
            return
        }

        // Konversi tanggal (String) ke Timestamp
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date: Date? = try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            Toast.makeText(this, "Format tanggal salah! Gunakan dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionData = hashMapOf(
            "tanggal" to date?.let { Timestamp(it) }, // Simpan sebagai Timestamp
            "jumlah" to amount, // Simpan sebagai Integer
            "jenis" to type,
            "kategori" to category,
            "instrumen" to instrument,
            "notes" to notes
        )

        firestore.collection("transactions")
            .add(transactionData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
