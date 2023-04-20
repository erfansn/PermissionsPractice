package ir.erfansn.permissionspractice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.erfansn.permissionspractice.databinding.ActivityMainBinding
import ir.erfansn.permissionspractice.type.OnDemandActivity
import ir.erfansn.permissionspractice.type.OnStartupActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.onStartup.setOnClickListener {
            startActivity(Intent(this, OnStartupActivity::class.java))
        }
        binding.onDemand.setOnClickListener {
            startActivity(Intent(this, OnDemandActivity::class.java))
        }
    }
}
