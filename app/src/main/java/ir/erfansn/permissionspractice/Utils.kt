package ir.erfansn.permissionspractice

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import android.os.Environment
import android.view.View
import com.google.android.material.snackbar.Snackbar

val Context.permissionsPreferences
    get() = getSharedPreferences(getString(R.string.preferences_permissions), MODE_PRIVATE)

fun View.showSnackbar(
    message: String,
    length: Int,
    actionMessage: String? = null,
    action: ((View) -> Unit)? = null,
) {
    val snackbar = Snackbar.make(this, message, length)
    snackbar.setAction(actionMessage, action)
    snackbar.show()
}

val hasAccessToAllFiles get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    Environment.isExternalStorageManager()
} else true
