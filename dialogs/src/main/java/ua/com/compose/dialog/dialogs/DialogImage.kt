package ua.com.compose.dialog.dialogs

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ua.com.compose.dialog.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.scope.lifecycleScope
import ua.com.compose.dialog.DialogManager
import ua.com.compose.dialog.databinding.DialogConfirmationBinding
import ua.com.compose.dialog.databinding.DialogImageBinding
import ua.com.compose.extension.*
import ua.com.compose.mvp.data.viewBindingWithBinder
import ua.com.compose.navigator.remove


class DialogImage : BottomSheetDialogFragment() {

    companion object {

        const val TAG = "DialogImage"
        private const val BUNDLE_KEY_URI = "BUNDLE_KEY_URI"
        private const val BUNDLE_KEY_MESSAGE = "BUNDLE_KEY_MESSAGE"
        private const val BUNDLE_KEY_REQUEST_KEY = "BUNDLE_KEY_REQUEST_KEY"

        fun show(fm: FragmentManager, uri: Uri, message: String? = null): String {
            val requestKey = System.currentTimeMillis().toString()
            val fragment = DialogImage().apply {
                this.arguments = bundleOf(
                    BUNDLE_KEY_URI to uri,
                    BUNDLE_KEY_REQUEST_KEY to requestKey,
                    BUNDLE_KEY_MESSAGE to message
                )
            }
            fm.remove(TAG)
            fragment.show(fm, TAG)
            return requestKey
        }
    }

    private val binding by viewBindingWithBinder(DialogImageBinding::bind)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            this.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            this.setCancelable(true)
            this.setCanceledOnTouchOutside(true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_image, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtMessage2.isVisible = false
        arguments?.takeIf { it.containsKey(BUNDLE_KEY_MESSAGE) }?.getString(BUNDLE_KEY_MESSAGE)?.let {
            binding.txtMessage2.isVisible = true
            binding.txtMessage2.text = it
        }

        (arguments?.getParcelable<Uri>(BUNDLE_KEY_URI))?.let {
            Glide.with(requireContext().applicationContext)
                .load(it)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.imageView)
        }

        binding.btnShare.setVibrate(EVibrate.BUTTON)
        binding.btnShare.setOnClickListener {
            (arguments?.getParcelable<Uri>(BUNDLE_KEY_URI))?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    context?.createImageIntent(it)
                }
            }
        }

        binding.btnLoad.setVibrate(EVibrate.BUTTON)
        binding.btnLoad.setOnClickListener {
            val context = context ?: return@setOnClickListener
            (arguments?.getParcelable<Uri>(BUNDLE_KEY_URI))?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    val bitmap = context.loadImage(it)
                    context.saveBitmap(bitmap)
                    withContext(Dispatchers.Main) {
                        context.let { it.toast(it.getString(R.string.module_dialog_save_ready)) }
                    }
                }
            }
        }
    }
}