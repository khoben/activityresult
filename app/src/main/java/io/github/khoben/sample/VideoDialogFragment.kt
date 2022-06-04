package io.github.khoben.sample

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import io.github.khoben.arpermission.sample.R

class VideoDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.video_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = arguments?.getParcelable<Uri>(EXTRA_VIDEO_URI)

        with(view.findViewById<VideoView>(R.id.video)) {
            setVideoURI(uri)
            start()
        }

    }

    companion object {
        val TAG: String = VideoDialogFragment::class.java.simpleName

        private const val EXTRA_VIDEO_URI = "VideoDialogFragment::extra_video_uri"

        fun create(videoUri: Uri?): VideoDialogFragment {
            return VideoDialogFragment().apply {
                arguments = bundleOf(EXTRA_VIDEO_URI to videoUri)
            }
        }
    }
}