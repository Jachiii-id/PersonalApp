package com.example.personalapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonalPost(
    val imageUrl: String = "",
    val caption: String = "",
    val date: String = ""
) : Parcelable
