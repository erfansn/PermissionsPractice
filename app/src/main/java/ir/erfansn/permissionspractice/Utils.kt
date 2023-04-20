package ir.erfansn.permissionspractice

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.view.View
import androidx.core.content.edit
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

val Context.permissionsPreferences: SharedPreferences
    get() = getSharedPreferences(getString(R.string.preferences_permissions), MODE_PRIVATE)

operator fun SharedPreferences.set(key: String, value: Boolean) = edit {
    putBoolean(key, value)
}

operator fun SharedPreferences.get(key: String, default: Boolean = false) =
    getBoolean(key, default)

fun ViewBinding.showSnackbar(
    message: String,
    length: Int,
    actionMessage: String? = null,
    action: ((View) -> Unit)? = null,
) {
    val snackbar = Snackbar.make(root, message, length)
    snackbar.setAction(actionMessage, action)
    snackbar.show()
}

val hasAccessToAllFiles
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        true
    }

val Context.canDrawOverlays get() = Settings.canDrawOverlays(this)
