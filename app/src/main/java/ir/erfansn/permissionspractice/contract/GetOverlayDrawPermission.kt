package ir.erfansn.permissionspractice.contract

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import ir.erfansn.permissionspractice.canDrawOverlays

class GetOverlayDrawPermission : ActivityResultContract<Unit, Boolean>() {

    private lateinit var context: Context

    override fun createIntent(context: Context, input: Unit): Intent {
        this.context = context.applicationContext
        return Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            .setData(Uri.fromParts("package", context.packageName, null))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return context.canDrawOverlays
    }

    override fun getSynchronousResult(context: Context, input: Unit): SynchronousResult<Boolean>? {
        return if (context.canDrawOverlays) SynchronousResult(true) else null
    }
}
