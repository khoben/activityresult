package io.github.khoben.sample

import android.Manifest
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import io.github.khoben.arpermission.PermissionRequest
import io.github.khoben.arpermission.sample.R
import io.github.khoben.arresult.launcher.GetContentUriLauncher

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val permissions by lazy { PermissionRequest() }
    private val imagePicker by lazy { GetContentUriLauncher() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissions.register(this)
        imagePicker.register(this)
        findViewById<Button>(R.id.buttonPermission).setOnClickListener {
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
        findViewById<Button>(R.id.buttonResult).setOnClickListener {
            imagePicker.launch("image/*") {
                success = { imageUri ->
                    imageUri?.let {
                        val drawable =
                            BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                                .toDrawable(resources)
                        findViewById<View>(R.id.root).background = drawable
                    }
                }
                failed = {
                    Toast.makeText(this@MainActivity, "Image picker failed: $it", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}