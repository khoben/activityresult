package io.github.khoben.arresult.launcher

import android.net.Uri
import io.github.khoben.arresult.contract.TakeVideoUriContract

/**
 * An `ActivityResultAPI` launcher to capture a video via camera application,
 * receiving a `content://` [Uri] for that video.
 *
 * The `input` is the output video content [Uri]. If `input` not present, the video will be
 * written to the standard location for videos, and the [Uri] of that location will be returned.
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
 * @see <a href="https://developer.android.com/reference/android/provider/MediaStore#ACTION_VIDEO_CAPTURE">MediaStore.ACTION_VIDEO_CAPTURE</a>
 */
class TakeVideoUriLauncher : BaseLauncher<Uri?, Uri?>(TakeVideoUriContract())
