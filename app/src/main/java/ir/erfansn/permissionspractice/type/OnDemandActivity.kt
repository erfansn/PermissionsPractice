package ir.erfansn.permissionspractice.type

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import ir.erfansn.permissionspractice.R
import ir.erfansn.permissionspractice.canDrawOverlays
import ir.erfansn.permissionspractice.contract.GetOverlayDrawPermission
import ir.erfansn.permissionspractice.databinding.ActivityOnDemandBinding
import ir.erfansn.permissionspractice.get
import ir.erfansn.permissionspractice.permissionsPreferences
import ir.erfansn.permissionspractice.set
import ir.erfansn.permissionspractice.showSnackbar

class OnDemandActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnDemandBinding

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                binding.showSnackbar(
                    getString(R.string.camera_permission_granted),
                    Snackbar.LENGTH_LONG,
                )
                Log.i(TAG, "Camera access permission granted")
            } else {
                Log.i(TAG, "Camera access permission denied")
            }
        }

    private val requestOverlayDrawPermission =
        registerForActivityResult(GetOverlayDrawPermission()) { isGranted ->
            if (isGranted) {
                binding.showSnackbar(
                    getString(R.string.overlay_draw_permission_granted),
                    Snackbar.LENGTH_LONG,
                )
                Log.i(TAG, "Overlay draw permission granted")
            } else {
                Log.i(TAG, "Overlay draw permission granted")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnDemandBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onClickRequestRuntimePermission(view: View) {
        when {
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            ) -> {
                binding.showSnackbar(
                    getString(R.string.camera_permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {
                    permissionsPreferences[getString(R.string.key_permissions_camera_rationale)] = true
                    requestCameraPermission.launch(
                        android.Manifest.permission.CAMERA
                    )
                }
            }

            permissionsPreferences[getString(R.string.key_permissions_camera_rationale)]
                    && !isCameraPermissionGranted -> {
                binding.showSnackbar(
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
                requestCameraPermission.launch(
                    android.Manifest.permission.CAMERA
                )
            }
        }
    }

    fun onClickRequestSpecialPermission(view: View) {
        if (!canDrawOverlays) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.overlay_draw_permission_required))
                .setMessage(getString(R.string.overlay_draw_permission_required_details))
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    requestOverlayDrawPermission.launch()
                }
                .show()
        } else {
            requestOverlayDrawPermission.launch()
        }
    }

    private val isCameraPermissionGranted
        get() = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val TAG = "OnDemandActivity"
    }
}
