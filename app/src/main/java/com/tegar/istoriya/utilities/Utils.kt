package com.tegar.istoriya.utilities

import android.content.Context
import android.view.View
import android.widget.Toast

object Utils {
    fun showLoading(view: View, isLoading: Boolean) {
        view.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
