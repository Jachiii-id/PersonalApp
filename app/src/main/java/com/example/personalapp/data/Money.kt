package com.example.personalapp.data

import com.google.firebase.Timestamp

data class Money(
    val instrumen: String = "",
    val jenis: String = "",
    val jumlah: Int = 0,  // Ubah ke Integer
    val kategori: String = "",
    val notes: String = "",
    val tanggal: Timestamp? = null
)
