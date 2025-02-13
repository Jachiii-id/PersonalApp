package com.example.personalapp.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale

@Parcelize
data class Money(
    val instrumen: String? = null,
    val jenis: String? = null,
    val jumlah: Int? = null,
    val kategori: String = "",
    val notes: String = "",
    val tanggal: Timestamp? = null
) : Parcelable {

    // Function to format the date into "MMMM yyyy" format (e.g., February 2025)
    fun getFormattedDate(): String {
        return tanggal?.toDate()?.let { date ->
            val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            sdf.format(date)
        } ?: ""  // Return an empty string if no date is available
    }
}
