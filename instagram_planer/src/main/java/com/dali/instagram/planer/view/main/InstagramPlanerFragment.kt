package com.dali.instagram.planer.view.main

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import com.dali.instagram.planer.R
import com.dali.instagram.planer.di.Scope
import com.inhelp.base.mvp.BaseMvpActivity
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.dialogs.main.dialogs.DialogAlert
import com.inhelp.dialogs.main.dialogs.DialogChip
import com.inhelp.dialogs.main.dialogs.DialogConfirmation
import com.inhelp.dialogs.main.dialogs.DialogInput
import com.inhelp.extension.scrollTo
import com.inhelp.gallery.main.FragmentGallery
import data.BottomMenu
import data.Menu
import kotlinx.android.synthetic.main.fragment_instagram_planer.*


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
        return inflater.inflate(R.layout.fragment_instagram_planer, container, false)
    }

    override fun createBottomMenu(): MutableList<Menu> {

        val btnGallery = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_gallery) {
            FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = true)
        }

        val btnAccountSetting = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_account_setting) {
            (activity as BaseMvpActivity<*, *>).setupBottomMenu(createAccountMenu())
        }

        return mutableListOf(btnAccountSetting, btnGallery)
    }

    private fun createAccountMenu(): MutableList<Menu> {
        val btnBack = BottomMenu(iconResId = com.inhelp.theme.R.drawable.ic_back) {
            (activity as BaseMvpActivity<*, *>).setupBottomMenu(createBottomMenu())
        }.apply {
            this.iconTintRes = R.attr.color_6
        }

        val btnAccountAdd = BottomMenu(iconResId = R.drawable.ic_instagram_planer_person_add) {
            createDialogInputName()
        }

        val btnAccountRemove = BottomMenu(iconResId = R.drawable.ic_instagram_planer_person_remove) {
            DialogAlert.show(fm = childFragmentManager, message = "Грейс — успешный терапевт из Нью-Йорка, у неё все хорошо: любящий муж, прекрасный сын, богатая жизнь. Скоро у Грейс выйдет первая книга.")
        }

        val btnAccountChange = BottomMenu(iconResId = R.drawable.ic_instagram_planer_switch_account) {
            DialogAlert.show(fm = childFragmentManager, message = "Грейс — успешный терапевт из Нью-Йорка, у неё все хорошо: любящий муж, прекрасный сын, богатая жизнь. Скоро у Грейс выйдет первая книга.")
        }

        val btnAccountClear = BottomMenu(iconResId = R.drawable.ic_instagram_planer_person_clear) {
            val request = DialogConfirmation.show(fm = getCurrentActivity().supportFragmentManager, message = "Грейс — успешный терапевт из Нью-Йорка, у неё все хорошо: любящий муж, прекрасный сын, богатая жизнь. Скоро у Грейс выйдет первая книга.")
            setFragmentResultListener(request) { _, bundle ->
                presenter.onAccountClearConfirm(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER))
            }
        }

        return mutableListOf(btnBack, btnAccountChange, btnAccountAdd, btnAccountRemove, btnAccountClear)
    }

    private fun initGridPreview() {
        gridList.layoutManager = GridLayoutManager(getCurrentContext(), 3)
        gridList.adapter = InstagramPlanerRvAdapter(
                images = presenter.images,
                onPress = {
//                    presenter.pressImage(it)
                },
                onChange = { oldPosition, newPosition ->
                    presenter.onChangeImagePosition(oldPosition, newPosition)
                })
    }

    override fun setWallName(value: String) {
        txtAccount.text = value
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

        btnMoreAccount.setOnClickListener {
            presenter.pressMoreAccount()
        }

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            presenter.onAddImages((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        presenter.onLoad()
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

    override fun backPress(): Boolean {
        backToMain()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        Scope.INSTAGRAM.close()
    }
}