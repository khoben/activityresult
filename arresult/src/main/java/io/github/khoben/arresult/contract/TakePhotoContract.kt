package io.github.khoben.arresult.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import io.github.khoben.arresult.launcher.TakePhotoResult

/**
 * [ActivityResultContract] that can be sent to have the camera application capture an image
 * and return it as [TakePhotoResult]:
 *
 * 1. [TakePhotoResult.Preview] (`input` @ [TakePhotoContract.createIntent] is null)
 * 2. [TakePhotoResult.FullSized] (`input` @ [TakePhotoContract.createIntent] is not null)
 *
 * @see <a href="https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE">MediaStore.ACTION_IMAGE_CAPTURE</a>
 */
internal class TakePhotoContract : ActivityResultContract<Uri?, TakePhotoResult?>() {
    private var requestedUri: Uri? = null

    override fun createIntent(context: Context, input: Uri?): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            if (input != null) {
                requestedUri = input
                putExtra(MediaStore.EXTRA_OUTPUT, input)
            }
        }
    }

    override fun getSynchronousResult(
        context: Context,
        input: Uri?
    ): SynchronousResult<TakePhotoResult?>? = null

    override fun parseResult(resultCode: Int, intent: Intent?): TakePhotoResult? {
        val previewBitmap: Bitmap? = intent?.getParcelableExtra("data")
        val contentUri: Uri? = requestedUri
        requestedUri = null
        return when {
            resultCode != Activity.RESULT_OK -> null
            contentUri != null -> TakePhotoResult.FullSized(contentUri)
            previewBitmap != null -> TakePhotoResult.Preview(previewBitmap)
            else -> null
        }
    }
}