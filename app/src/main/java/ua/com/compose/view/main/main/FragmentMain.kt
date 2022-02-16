package ua.com.compose.view.main.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import ua.com.compose.R
import kotlinx.android.synthetic.main.fragment_main.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_image_info.*
import kotlinx.android.synthetic.main.fragment_main.btnRemove
import kotlinx.android.synthetic.main.fragment_main.btnSave
import kotlinx.android.synthetic.main.fragment_main.btnShareImage
import kotlinx.android.synthetic.main.fragment_main.container
import kotlinx.android.synthetic.main.fragment_main.imgPreview
import ua.com.compose.mvp.BaseMvpFragment
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import ua.com.compose.analytics.Analytics
import ua.com.compose.analytics.Event
import ua.com.compose.analytics.SimpleEvent
import ua.com.compose.analytics.analytics
import ua.com.compose.extension.EVibrate
import ua.com.compose.extension.createImageIntent
import ua.com.compose.extension.setVibrate
import ua.com.compose.image_compress.main.ImageCompressFragment
import ua.com.compose.navigator.replace
import ua.com.compose.view.main.MainActivity


class FragmentMain : BaseMvpFragment<ViewMain, PresenterMain>(), ViewMain {

    companion object {
        private const val BUNDLE_KEY_IMAGE_URI = "BUNDLE_KEY_IMAGE_URI"

        fun newInstance(uri: Uri?): FragmentMain {
            return FragmentMain().apply {
                arguments = bundleOf(
                        BUNDLE_KEY_IMAGE_URI to uri
                )
            }
        }
    }

    override val presenter: PresenterMain by lazy {
        val scope = getKoin().getOrCreateScope(
                "app", named("app"))
        scope.get()
    }

    private lateinit var mMenuRvAdapter: MenuRvAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_menu), requireContext().getDrawable(R.drawable.ic_logo_vector))
        setVisibleBack(false)
        (requireActivity() as MainActivity).setVisibleHeader(true)
        initList()

        setFragmentResultListener(ImageCompressFragment.REQUEST_KEY) { _, bundle ->
            presenter.addImage(bundle.getParcelable<Uri>(ImageCompressFragment.BUNDLE_KEY_IMAGE_URI))
        }

        btnSave.setVibrate(EVibrate.BUTTON)
        btnSave.setOnClickListener {
            presenter.pressSave()
        }

        btnRemove.setVibrate(EVibrate.BUTTON)
        btnRemove.setOnClickListener {
            presenter.pressRemove()
        }

        btnShareImage.setVibrate(EVibrate.BUTTON)
        btnShareImage.setOnClickListener {
            presenter.imageHolder.image?.let {
                analytics.send(event = SimpleEvent(
                    key = Analytics.Event.IMAGE_SHARE
                ))
                requireActivity().createImageIntent(it)
            }
        }

        imgPreview.setVibrate(EVibrate.BUTTON)
        imgPreview.setOnClickListener {
            requireActivity().supportFragmentManager.replace(
                fragment = ua.com.compose.other_image_info.main.ImageInfoFragment.newInstance(uri = presenter.imageHolder.image),
                addToBackStack = true
            )
        }
        val inputUri = arguments?.getParcelable(BUNDLE_KEY_IMAGE_URI) as? Uri
        presenter.onCreate(uri = inputUri)
        presenter.updateList()
    }

    override fun setVisibleImage(isVisible: Boolean) {
        container.isVisible = isVisible
    }

    override fun setImage(uri: Uri) {
        container.isVisible = true
        Glide.with(requireContext()).load(uri).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).centerInside().thumbnail(0.1f).into(imgPreview)
    }

    override fun onDestroyView() {
        (requireActivity() as MainActivity).setVisibleHeader(false)
        super.onDestroyView()
    }

    private fun initList() {
        menuList.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        mMenuRvAdapter = MenuRvAdapter(presenter.getOrCreateMenu(fm = requireActivity().supportFragmentManager)){
            analytics.send(event = Event(
                key = Analytics.Event.MAIN_MENU,
                params = arrayOf("name" to it.name)
            ))
            (getCurrentActivity() as MainActivity).setVisibleHeader(false)
        }
        menuList?.adapter = mMenuRvAdapter
    }

    override fun updatePhotoList(){
    }
}