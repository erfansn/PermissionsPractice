package ir.erfansn.permissions

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ir.erfansn.permissions.databinding.ActivityMainBinding
import ir.erfansn.permissions.type.OnDemandActivity
import ir.erfansn.permissions.type.StartupActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startup.setOnClickListener {
            startActivity(Intent(this, StartupActivity::class.java))
        }
        binding.onDemand.setOnClickListener {
            startActivity(Intent(this, OnDemandActivity::class.java))
        }
    }
}
