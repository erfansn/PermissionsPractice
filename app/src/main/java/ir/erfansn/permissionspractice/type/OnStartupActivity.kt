package ir.erfansn.permissionspractice.type

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import ir.erfansn.permissionspractice.R
import ir.erfansn.permissionspractice.contract.GetAllFileAccessPermission
import ir.erfansn.permissionspractice.databinding.ActivityOnStartupBinding
import ir.erfansn.permissionspractice.get
import ir.erfansn.permissionspractice.hasAccessToAllFiles
import ir.erfansn.permissionspractice.permissionsPreferences
import ir.erfansn.permissionspractice.set
import ir.erfansn.permissionspractice.showSnackbar

class OnStartupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnStartupBinding

    private val requestSmsPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i(TAG, "Read SMS permission granted")
            } else {
                Log.i(TAG, "Read SMS permission denied")
            }
        }

    @RequiresApi(Build.VERSION_CODES.R)
    private val requestAllFileAccessPermission =
        registerForActivityResult(GetAllFileAccessPermission()) { isGranted ->
            if (isGranted) {
                Log.i(TAG, "All file access permission granted")
            } else {
                Log.i(TAG, "All file access permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnStartupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        when {
            isSmsPermissionGranted && hasAccessToAllFiles -> {
                binding.showSnackbar(
                    getString(R.string.all_permissions_granted),
                    Snackbar.LENGTH_LONG,
                )
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.READ_SMS
            ) -> {
                permissionsPreferences[getString(R.string.key_permissions_sms_rationale)] = true

                binding.showSnackbar(
                    getString(R.string.sms_permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok),
                ) {
                    requestSmsPermission.launch(
                        android.Manifest.permission.READ_SMS
                    )
                }
            }

            // Because showing rationale message to user has high priority of direct to settings
            // app this condition listed below it
            permissionsPreferences[getString(R.string.key_permissions_sms_rationale)] && !isSmsPermissionGranted -> {
                binding.showSnackbar(
                    getString(R.string.allowing_permission_from_settings, "SMS"),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.go)
                ) {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", packageName, null))
                        .also(::startActivity)
                }
            }

            // Default value is true because must first show rationale message to user before
            // direct user to settings to grant permission by best practice in Special permission
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !hasAccessToAllFiles
                    && permissionsPreferences[getString(R.string.key_permissions_all_file_access_rationale), true]
            -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.manage_storage_permission_required))
                    .setMessage(getString(R.string.manage_storage_permission_required_details))
                    .setPositiveButton(getString(R.string.ok)) @RequiresApi(Build.VERSION_CODES.R) { _, _ ->
                        requestAllFileAccessPermission.launch()
                    }
                    .setOnCancelListener {
                        finish()
                    }
                    .show()
            }

            // To follow DTY principle, I'm using [ActivityResultContract]s to prevent write
            // duplicate check to execute desired code after it, if you need some code to be
            // run on first granted signal can use a temp boolean variable or you want only one
            // utilize with persistent storages.
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestAllFileAccessPermission.launch()
                }
                requestSmsPermission.launch(android.Manifest.permission.READ_SMS)
            }
        }
    }

    private val isSmsPermissionGranted
        get() = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_SMS,
        ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val TAG = "OnStartupActivity"
    }
}
