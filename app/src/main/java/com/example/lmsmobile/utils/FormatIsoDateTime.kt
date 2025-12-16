package com.example.lmsmobile.util

import java.text.SimpleDateFormat
import java.util.*

fun formatIsoDateTime(input: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val date = parser.parse(input)
        if (date != null) formatter.format(date) else input
    } catch (e: Exception) {
        input // fallback to raw string if parsing fails
    }
}