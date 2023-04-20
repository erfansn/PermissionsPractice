package ir.erfansn.permissionspractice.contract

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import ir.erfansn.permissionspractice.hasAccessToAllFiles

@RequiresApi(Build.VERSION_CODES.R)
class GetAllFileAccessPermission : ActivityResultContract<Unit, Boolean>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            .setData(Uri.fromParts("package", context.packageName, null))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return hasAccessToAllFiles
    }

    override fun getSynchronousResult(context: Context, input: Unit): SynchronousResult<Boolean>? {
        return if (hasAccessToAllFiles) SynchronousResult(true) else null
    }
}
