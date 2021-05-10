package ua.com.compose.gallery.main.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.module_gallery_fragment_gallery_content.*
import ua.com.compose.mvp.bottomSheetFragment.BaseMvpBottomSheetFragment
import ua.com.compose.gallery.R
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import ua.com.compose.extension.updateVisibleItem


class FragmentGalleryContent : BaseMvpBottomSheetFragment<ViewGalleryContent, PresenterGalleryContent>(), ViewGalleryContent {

    companion object {

        private const val FOLDER_POSITION = "FOLDER_POSITION"

        fun newInstance(position: Int): FragmentGalleryContent {
            return FragmentGalleryContent().apply {
                this.arguments = Bundle().apply {
                    this.putInt(FOLDER_POSITION, position)
                }
            }
        }
    }

    override val presenter: PresenterGalleryContent by lazy {
        val scope = getKoin().getOrCreateScope(
                "gallery", named("gallery"))
        scope.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_gallery_fragment_gallery_content, container, false)
    }

    private fun initList(){
        list.layoutManager = GridLayoutManager(getCurrentContext(),3, RecyclerView.VERTICAL, false)
        list.adapter = GalleryRvAdapter(
                requireContext(),
                presenter.images,
                presenter.selectedImages,
                onPress = { uri, isLongPress ->
                    presenter.pressImage(uri = uri, isMultiSelect = isLongPress)
                },
                onUpdateBadge = {
                    list?.updateVisibleItem(GalleryRvAdapter.CHANGE_BADGE)
                }
        )
    }

    fun clearSelectSelect(){
        list?.updateVisibleItem(GalleryRvAdapter.CHANGE_CLEAR_SELECT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.init(position = this.arguments?.getInt(FOLDER_POSITION) ?: 0)
        list.post {
            initList()
        }
    }

}