package io.github.khoben.sample

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
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

    private val permissions = PermissionRequest()
    private val imagePicker = GetContentUriLauncher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissions.register(this)
        imagePicker.register(this)

        findViewById<Button>(R.id.buttonPermission).setOnClickListener {
            permissions.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                onDenied = { denied, isCancelled -> showToast("onDenied $denied $isCancelled") },
                onExplained = { explained -> showToast("onExplained $explained") },
                onGranted = { showToast("Granted") }
            )
        }

        findViewById<Button>(R.id.buttonResult).setOnClickListener {
            imagePicker.launch("image/*") {
                success = { imageUri -> setImageAsBackground(imageUri) }
                failed = { cause -> showToast("Image picker failed: $cause") }
            }
        }
    }

    private fun setImageAsBackground(imageUri: Uri?) {
        if (imageUri == null) {
            showToast("Image uri was null")
            return
        }

        findViewById<View>(R.id.root).background = BitmapFactory
            .decodeStream(contentResolver.openInputStream(imageUri))
            .toDrawable(resources)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}