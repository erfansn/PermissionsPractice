package ir.erfansn.permissions.type

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import com.google.android.material.snackbar.Snackbar
import ir.erfansn.permissions.R
import ir.erfansn.permissions.databinding.ActivityOnDemandBinding
import ir.erfansn.permissions.hasAccessToAllFiles
import ir.erfansn.permissions.permissionsPreferences
import ir.erfansn.permissions.showSnackbar

class OnDemandActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnDemandBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.i(TAG, "Granted")
            } else {
                Log.i(TAG, "Denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnDemandBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED && hasAccessToAllFiles) {
            binding.root.showSnackbar(
                getString(R.string.all_permissions_granted),
                Snackbar.LENGTH_LONG,
            )
        }
    }

    fun onClickRequestRuntimePermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED -> {
                binding.root.showSnackbar(
                    getString(R.string.camera_permission_granted),
                    Snackbar.LENGTH_LONG,
                )
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            ) -> {
                binding.root.showSnackbar(
                    getString(R.string.camera_permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {
                    permissionsPreferences.edit {
                        putBoolean(getString(R.string.key_permissions_camera_rationale), true)
                    }
                    requestPermissionLauncher.launch(
                        android.Manifest.permission.CAMERA
                    )
                }
            }
            permissionsPreferences.getBoolean(
                getString(R.string.key_permissions_camera_rationale),
                false
            ) -> {
                binding.root.showSnackbar(
                    getString(R.string.allowing_permission_from_settings, "Camera"),
                    Snackbar.LENGTH_LONG,
                    getString(R.string.go)
                ) {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", packageName, null))
                        .also(::startActivity)
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.CAMERA
                )
            }
        }
    }

    fun onClickRequestSpecialPermission(view: View) {
        if (hasAccessToAllFiles) {
            binding.root.showSnackbar(
                getString(R.string.manage_storage_permission_granted),
                Snackbar.LENGTH_INDEFINITE,
            )
        } else {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.manage_storage_permission_required))
                .setMessage(getString(R.string.manage_storage_permission_required_details))
                .setPositiveButton(getString(R.string.ok)) @RequiresApi(Build.VERSION_CODES.R) { _, _ ->
                    Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        .setData(Uri.fromParts("package", packageName, null))
                        .also(::startActivity)
                }
                .show()
        }
    }

    companion object {
        private const val TAG = "OnDemandActivity"
    }
}
