package ua.com.compose.instagram_no_crop.view.save

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import com.bumptech.glide.Glide
import ua.com.compose.instagram_no_crop.R
import com.bumptech.glide.load.engine.DiskCacheStrategy
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.dialog.dialogs.DialogColor
import ua.com.compose.extension.*
import ua.com.compose.instagram_no_crop.di.Scope
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import kotlinx.android.synthetic.main.module_instagram_no_crop_fragment_instagram_no_crop_share.*
import ua.com.compose.navigator.back

class InstagramCropSaveFragment : BaseMvpFragment<InstagramCropSaveView, InstagramCropSavePresenter>(), InstagramCropSaveView {

    companion object {
        fun newInstance() = InstagramCropSaveFragment()
    }

    override val presenter: InstagramCropSavePresenter by lazy {
        Scope.INSTAGRAM.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_instagram_no_crop_fragment_instagram_no_crop_share, container, false)
    }

    private val btnDownload by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_download) {
            presenter.pressSave()
        }
    }

    private val btnInstagram by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_instagram) {
            presenter.pressInstagram()
        }
    }

    private val btnBack by lazy {
        BottomMenu(iconResId = ua.com.compose.R.drawable.ic_back) {
            requireActivity().supportFragmentManager.back()
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf<Menu>().apply {
            this.add(btnBack)
            this.add(btnInstagram)
            this.add(btnDownload)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_instagram_no_crop_fragment_title_crop))

        btnBlur.setVibrate(EVibrate.BUTTON)
        btnBlur.setOnClickListener {
            presenter.pressBlur()
        }

        btnColorFill.setVibrate(EVibrate.BUTTON)
        btnColorFill.setOnClickListener {
            val request = DialogColor.show(fm = getCurrentActivity().supportFragmentManager, color = Color.RED)
            setFragmentResultListener(request) { _, bundle ->
                presenter.onColorPick(color = bundle.getInt(DialogColor.BUNDLE_KEY_ANSWER_COLOR))
            }
        }

        presenter.onLoadImage()
    }

    override fun setImageBitmap(bitmap: Bitmap) {
        Glide
                .with(imgView.context)
                .load(bitmap)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(0.1f)
                .into(imgView)
    }

    override fun createShareIntent(uri: Uri) {
        getCurrentActivity().createImageIntent(uri)
    }

    override fun createInstagramIntent(uri: Uri) {
        getCurrentActivity().createInstagramIntent(uri)
    }

    override fun setImageBackground(bitmap: Bitmap) {
        imgView.background = bitmap.toDrawable(getCurrentContext().resources)
    }

    override fun setVisibleEdit(isVisible: Boolean) {
        btnBlur.isVisible = isVisible
        btnColorFill.isVisible = isVisible
    }

    override fun backPress(byBack: Boolean): Boolean {
        if(byBack){
            backToMain()
            Scope.INSTAGRAM.close()
        }else{
            return false
        }
        return true
    }
}