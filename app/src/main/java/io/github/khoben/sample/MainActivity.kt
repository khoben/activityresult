package io.github.khoben.sample

import android.Manifest
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.github.khoben.arpermission.PermissionRequest
import io.github.khoben.arpermission.sample.R

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val permissions by lazy { PermissionRequest() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissions.register(this)
        findViewById<TextView>(R.id.textView).setOnClickListener {
            permissions.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                onDenied = { permissions, isCancelled ->
                    Toast.makeText(this, "onDenied $permissions $isCancelled", Toast.LENGTH_SHORT)
                        .show()
                },
                onExplained = {
                    Toast.makeText(this, "onExplained $it", Toast.LENGTH_SHORT).show()
                }
            ) { // onGranted
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }
}