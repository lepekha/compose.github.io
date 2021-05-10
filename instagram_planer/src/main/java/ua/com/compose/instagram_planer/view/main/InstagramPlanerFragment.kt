package ua.com.compose.instagram_planer.view.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.module_instagram_planer_fragment_instagram_planer.*
import ua.com.compose.instagram_planer.di.Scope
import ua.com.compose.instagram_planer.view.image.InstagramPlanerImageFragment
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.dialog.dialogs.DialogChip
import ua.com.compose.dialog.dialogs.DialogConfirmation
import ua.com.compose.dialog.dialogs.DialogInput
import ua.com.compose.extension.scrollTo
import ua.com.compose.extension.setDrawableRight
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.navigator.replace
import ua.com.compose.instagram_planer.R


class InstagramPlanerFragment : BaseMvpFragment<InstagramPlanerView, InstagramPlanerPresenter>(), InstagramPlanerView {

    companion object {
        fun newInstance(): InstagramPlanerFragment {
            return InstagramPlanerFragment()
        }
    }

    override val presenter: InstagramPlanerPresenter by lazy {
        Scope.INSTAGRAM.get()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_instagram_planer_fragment_instagram_planer, container, false)
    }

    private val btnGallery = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_gallery) {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = true)
    }

    private val btnAccountAdd = BottomMenu(iconResId = R.drawable.module_instagram_planer_ic_instagram_planer_person_add) {
        createDialogInputName()
    }

    private val btnAccountRemove = BottomMenu(iconResId = R.drawable.module_instagram_planer_ic_instagram_planer_person_remove) {
        val request = DialogConfirmation.show(fm = requireActivity().supportFragmentManager, message = "Remove current account?")
        setFragmentResultListener(request) { _, bundle ->
            presenter.onRemoveAccount(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER))
        }
    }

    private val btnAccountClear = BottomMenu(iconResId = R.drawable.module_instagram_planer_ic_instagram_planer_person_clear) {
        val request = DialogConfirmation.show(fm = requireActivity().supportFragmentManager, message = "Clear current account?")
        setFragmentResultListener(request) { _, bundle ->
            presenter.onAccountClearConfirm(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER))
        }
    }

    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf(btnGallery, btnAccountAdd, btnAccountRemove, btnAccountClear)
    }

    private fun initGridPreview() {
        gridList.layoutManager = GridLayoutManager(getCurrentContext(), 3)
        gridList.adapter = InstagramPlanerRvAdapter(
                images = presenter.images,
                onPress = {
                    presenter.pressImage(it)
                },
                onChange = { oldPosition, newPosition ->
                    presenter.onChangeImagePosition(oldPosition, newPosition)
                })
    }

    override fun setWallName(value: String) {
        txtAccount.text = value
    }

    override fun setVisiblePlaceholder(isVisible: Boolean) {
        imgPlaceholder.isVisible = isVisible
    }

    override fun setVisibleClearAll(isVisible: Boolean) {
        btnAccountClear.isVisible = isVisible
        createBottomMenu()
    }

    override fun setVisibleRemoveAcc(isVisible: Boolean) {
        btnAccountRemove.isVisible = isVisible
    }

    override fun setVisibleAccountMoreIcon(isVisible: Boolean) {
        if(isVisible){
            txtAccount.setDrawableRight(R.drawable.ic_expand_more)
            txtAccount.isClickable = true
        }else{
            txtAccount.setDrawableRight(0)
            txtAccount.isClickable = false
        }
    }

    override fun updateList() {
        gridList.adapter?.notifyDataSetChanged()
    }

    override fun changeImageInList(position: Int) {
        gridList.adapter?.notifyItemChanged(position, InstagramPlanerRvAdapter.CHANGE_ITEM_POSITION)
    }

    override fun addImageToList(count: Int){
        gridList.adapter?.notifyItemRangeInserted(0, count)
        gridList.scrollTo(0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_instagram_palaner_title))

        initGridPreview()

        txtAccount.setOnClickListener {
            presenter.pressMoreAccount()
        }

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            presenter.onAddImages((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        presenter.loadUsers()
    }

//    override fun createInstagramIntent(uri: Uri) {
//        getCurrentActivity().createInstagramIntent(uri)
//    }

    override fun createDialogInputName(){
        val request = DialogInput.show(fm = getCurrentActivity().supportFragmentManager, message = getCurrentActivity().getString(R.string.module_instagram_palaner_wall_name))
        setFragmentResultListener(request) { _, bundle ->
            presenter.onInputName(bundle.getString(DialogInput.BUNDLE_KEY_INPUT_MESSAGE))
        }
    }

    override fun createDialogList(list: List<String>, select: String){
        val request = DialogChip.show(fm = getCurrentActivity().supportFragmentManager, list = list, selected = select)
        setFragmentResultListener(request) { _, bundle ->
            presenter.pressListAccount(bundle.getInt(DialogChip.BUNDLE_KEY_ANSWER_POSITION))
        }
    }

    override fun goToImage(uri: Uri) {
        getCurrentActivity().supportFragmentManager.replace(fragment = InstagramPlanerImageFragment.newInstance(uri = uri), addToBackStack = true)
    }

    override fun backPress(): Boolean {
        backToMain()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }
}