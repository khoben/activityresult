package io.github.khoben.arresult.launcher

import android.graphics.Bitmap
import android.net.Uri
import io.github.khoben.arresult.contract.TakePhotoContract

/**
 * Result of [TakePhotoLauncher]
 */
sealed class TakePhotoResult {
    /**
     * Small-sized [Bitmap] image
     */
    class Preview(val data: Bitmap) : TakePhotoResult()

    /**
     * [Uri] of full-sized image
     */
    class FullSized(val data: Uri) : TakePhotoResult()
}

/**
 * An `ActivityResultAPI` launcher to capture a image via camera application,
 * receiving a [TakePhotoResult] with `content://` [Uri] or [small-sized preview][Bitmap]
 * for that image.
 *
 * The `input` is the output image content [Uri]. If `input` not present, the image will be
 * written to the standard location for images, and the
 * [small-sized preview][TakePhotoResult.Preview] of that image will be returned.
 *
 * **Note**: if you app targets [M][android.os.Build.VERSION_CODES.M] and above and declares as using the
 * [android.Manifest.permission.CAMERA] permission which is not granted, then attempting to use this action
 * will result in a [SecurityException].
 *
 * Also without the specified `input` [Uri] on devices with [Android Q (API 29)][android.os.Build.VERSION_CODES.Q]
 * and above ([Scoped Storage limitation](https://developer.android.com/about/versions/11/privacy/storage))
 * can lead to **[IllegalStateException][java.lang.IllegalStateException]: Only owner is able to interact
 * with pending item `content://...`** while reading content [Uri].
 *
 * @see <a href="https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE">MediaStore.ACTION_IMAGE_CAPTURE</a>
 */
class TakePhotoLauncher :
    BaseLauncher<Uri?, TakePhotoResult?>(TakePhotoContract())

