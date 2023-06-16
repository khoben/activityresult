package io.github.khoben.sample

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import io.github.khoben.arpermission.PermissionRequest
import io.github.khoben.arpermission.permission.ConditionalPermission
import io.github.khoben.arpermission.sample.R
import io.github.khoben.arresult.launcher.GetContentUriLauncher
import io.github.khoben.arresult.launcher.TakePhotoLauncher
import io.github.khoben.arresult.launcher.TakePhotoResult
import io.github.khoben.arresult.launcher.TakeVideoUriLauncher

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val permissions = PermissionRequest()
    private val imagePicker = GetContentUriLauncher()
    private val takePhoto = TakePhotoLauncher()
    private val takeVideo = TakeVideoUriLauncher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissions.register(this)
        imagePicker.register(this)
        takePhoto.register(this)
        takeVideo.register(this)

        findViewById<Button>(R.id.buttonPermission).setOnClickListener {
            permissions.launch(
                ConditionalPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q
                ),
                Manifest.permission.CAMERA,
                onDenied = { denied, isCancelled -> showToast("onDenied $denied $isCancelled") },
                onExplained = { explained -> showToast("onExplained $explained") },
                onGranted = { showToast("Granted") }
            )
        }

        findViewById<Button>(R.id.buttonPickImage).setOnClickListener {
            imagePicker.launch("image/*") {
                success = { imageUri -> setImageAsBackground(imageUri) }
                failed = { cause -> showToast("Image picker failed: $cause") }
            }
        }

        findViewById<Button>(R.id.buttonTakePhoto).setOnClickListener {
            takePhoto.launch {
                success = { takePhotoResult ->
                    when (takePhotoResult) {
                        is TakePhotoResult.FullSized -> setImageAsBackground(takePhotoResult.data)
                        is TakePhotoResult.Preview -> setImageAsBackground(takePhotoResult.data)
                    }
                }
                failed = { cause ->
                    showToast("Take photo failed: $cause")
                    Log.e("takePhoto", "Take photo failed", cause)
                }
            }
        }

        findViewById<Button>(R.id.buttonTakeVideo).setOnClickListener {
            takeVideo.launch(generateUri(applicationContext, Environment.DIRECTORY_MOVIES)) {
                success = { takeVideoResult ->
                    VideoDialogFragment.create(takeVideoResult)
                        .show(supportFragmentManager, VideoDialogFragment.TAG)
                }
                failed = { cause ->
                    showToast("Take video failed: $cause")
                    Log.e("takeVideo", "Take video failed", cause)
                }
            }
        }
    }

    private fun setImageAsBackground(image: Bitmap) {
        findViewById<View>(R.id.root).background = image.toDrawable(resources)
    }

    private fun setImageAsBackground(imageUri: Uri) {
        findViewById<View>(R.id.root).background = BitmapFactory
            .decodeStream(contentResolver.openInputStream(imageUri))
            .toDrawable(resources)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}