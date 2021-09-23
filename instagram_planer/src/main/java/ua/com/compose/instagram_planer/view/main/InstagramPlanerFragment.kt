package ua.com.compose.instagram_planer.view.main

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.module_instagram_planer_fragment_instagram_planer.*
import kotlinx.android.synthetic.main.module_instagram_planer_fragment_instagram_planer.chipGroup
import kotlinx.android.synthetic.main.module_instagram_planer_fragment_instagram_planer_image.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import ua.com.compose.instagram_planer.di.Scope
import ua.com.compose.instagram_planer.view.image.InstagramPlanerImageFragment
import ua.com.compose.mvp.BaseMvpFragment
import ua.com.compose.dialog.dialogs.DialogChip
import ua.com.compose.dialog.dialogs.DialogConfirmation
import ua.com.compose.dialog.dialogs.DialogInput
import ua.com.compose.extension.*
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.navigator.replace
import ua.com.compose.instagram_planer.R
import ua.com.compose.mvp.BaseMvpActivity
import ua.com.compose.mvp.BaseMvvmFragment


class InstagramPlanerFragment: BaseMvvmFragment() {

    companion object {
        fun newInstance(): InstagramPlanerFragment {
            return InstagramPlanerFragment()
        }
    }

    private val viewModel: InstagramPlanerViewModel by lazy {
        Scope.INSTAGRAM.get()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.module_instagram_planer_fragment_instagram_planer, container, false)
    }

    private val btnGallery = BottomMenu(iconResId = ua.com.compose.R.drawable.ic_gallery) {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = true)
    }

    private val btnAddBox = BottomMenu(iconResId = R.drawable.module_instagram_planer_ic_instagram_planer_add_box) {
        FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = true)
    }

//    private val btnAccountClear = BottomMenu(iconResId = R.drawable.module_instagram_planer_ic_instagram_planer_person_clear) {
//        val request = DialogConfirmation.show(fm = requireActivity().supportFragmentManager, message = "Clear current account?")
//        setFragmentResultListener(request) { _, bundle ->
//            viewModel.onAccountClearConfirm(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER))
//        }
//    }


    override fun createBottomMenu(): MutableList<Menu> {
        return mutableListOf(btnGallery, btnAddBox)
    }

    private fun initGridPreview() {
        gridList.layoutManager = GridLayoutManager(getCurrentContext(), 3)
        gridList.adapter = InstagramPlanerRvAdapter(
                onPress = {
                    viewModel.pressImage(it)
                },
                onChange = { oldPosition, newPosition ->
                    viewModel.onChangeImagePosition(oldPosition, newPosition)
                },
                onStartDrag = {
                    setVisibleBottomMenu(isVisible = false)
                    groupButton.isVisible = true
                },
                onEndDrag = {
                    setVisibleBottomMenu(isVisible = true)
                    groupButton.isVisible = false
                }
        )
    }

    fun changeImageInList(position: Int) {
        gridList.adapter?.notifyItemChanged(position, InstagramPlanerRvAdapter.CHANGE_ITEM_POSITION)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_instagram_palaner_title))

        initGridPreview()

        txtAccount.setOnClickListener {
            viewModel.pressMoreAccount()
        }

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            viewModel.onAddImages((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        viewModel.images.observe(this){
            (gridList.adapter as InstagramPlanerRvAdapter).updateImages(images = it)
        }

        viewModel.isVisiblePlaceHolder.observe(this){
            imgPlaceholder.isVisible = it
        }

        viewModel.isVisibleMoreAccount.observe(this){
            if(it){
                txtAccount.setDrawableRight(R.drawable.ic_expand_more)
                txtAccount.isClickable = true
            }else{
                txtAccount.setDrawableRight(0)
                txtAccount.isClickable = false
            }
        }

        viewModel.userName.observe(this){
            txtAccount.text = it
        }

        viewModel.isVisibleClearAll.observe(this){
            btnClear.isEnabled = it
            btnClear.isClickable = it
            btnClear.alpha = if(it) 1f else 0.5f
        }

        viewModel.isVisibleRemoveAccount.observe(this){
            btnRemoveUser.isEnabled = it
            btnRemoveUser.isClickable = it
            btnRemoveUser.alpha = if(it) 1f else 0.5f
        }

        viewModel.createDialogInputName.observe(this){
            createDialogInputName(text = it)
        }

        viewModel.createUsersList.observe(this){
            showUsers(it)
        }

        viewModel.loadUsers()

        btnRemove.setOnDragListener { _, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    requireContext().vibrate(type = EVibrate.BUTTON)
                    btnRemove.animateScale(toScale = 1.5f)
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    btnRemove.animateScale(toScale = 1f)
                }
                DragEvent.ACTION_DROP -> {
                    btnRemove.animateScale(toScale = 1f)
                    groupButton.isVisible = false
                }
            }
            true
        }

        btnSave.setOnDragListener { _, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    requireContext().vibrate(type = EVibrate.BUTTON)
                    btnSave.animateScale(toScale = 1.5f)
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    btnSave.animateScale(toScale = 1f)
                }
                DragEvent.ACTION_DROP -> {
                    btnSave.animateScale(toScale = 1f)
                    groupButton.isVisible = false
                }
            }
            true
        }

        btnShare.setOnDragListener { _, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    requireContext().vibrate(type = EVibrate.BUTTON)
                    btnShare.animateScale(toScale = 1.5f)
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    btnShare.animateScale(toScale = 1f)
                }
                DragEvent.ACTION_DROP -> {
                    btnShare.animateScale(toScale = 1f)
                    groupButton.isVisible = false
                }
            }
            true
        }

        btnMore.setVibrate(EVibrate.BUTTON)
        btnMore.setOnClickListener {
            groupAccount.isVisible = !groupAccount.isVisible
            if(groupAccount.isVisible){
                imgMore.setImageResource(R.drawable.ic_expand_less)
            }else{
                imgMore.setImageResource(R.drawable.ic_expand_more)
            }
        }

        materialCardView.setVibrate(EVibrate.BUTTON)
        materialCardView.setOnClickListener {
            groupAccount.isVisible = !groupAccount.isVisible
            if(groupAccount.isVisible){
                imgMore.setImageResource(R.drawable.ic_expand_less)
            }else{
                imgMore.setImageResource(R.drawable.ic_expand_more)
            }
        }

        btnAddUser.setVibrate(EVibrate.BUTTON)
        btnAddUser.setOnClickListener {
            viewModel.pressAddUser()
        }

        btnClear.setVibrate(EVibrate.BUTTON)
        btnClear.setOnClickListener {
            val request = DialogConfirmation.show(fm = requireActivity().supportFragmentManager, message = "Clear current account?")
            setFragmentResultListener(request) { _, bundle ->
                viewModel.onAccountClearConfirm(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER))
            }
        }

        btnRemoveUser.setVibrate(EVibrate.BUTTON)
        btnRemoveUser.setOnClickListener {
            val request = DialogConfirmation.show(fm = requireActivity().supportFragmentManager, message = "Remove current account?")
            setFragmentResultListener(request) { _, bundle ->
                viewModel.onRemoveAccount(bundle.getBoolean(DialogConfirmation.BUNDLE_KEY_ANSWER))
            }
        }

        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.onAccountChange((group.get(checkedId) as Chip).text.toString())
        }
    }

    private fun showUsers(list: List<String>){
        chipGroup.removeAllViews()
        list.forEachIndexed { index, it ->
        val chip = Chip(context).apply {
            this.text = it
            this.chipBackgroundColor = ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.color_12))
            this.isCheckable = true
            this.id = index
            this.isChipIconVisible = false
            this.isCheckedIconVisible = false
            this.isClickable = true
            this.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_done)
            this.chipIconTint = ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.color_10))
            this.setTextColor(requireContext().getColorFromAttr(R.attr.color_10))
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, 18.sp)
        }
            chipGroup.addView(chip)
        }

    }

//    override fun createInstagramIntent(uri: Uri) {
//        getCurrentActivity().createInstagramIntent(uri)
//    }

    private fun createDialogInputName(text: String?){
        text ?: return
        val request = DialogInput.show(fm = getCurrentActivity().supportFragmentManager, text = text)
        setFragmentResultListener(request) { _, bundle ->
            viewModel.onInputName(bundle.getString(DialogInput.BUNDLE_KEY_INPUT_MESSAGE))
        }
    }

    fun createDialogList(list: List<String>, select: String){
        val request = DialogChip.show(fm = getCurrentActivity().supportFragmentManager, list = list, selected = select)
        setFragmentResultListener(request) { _, bundle ->
            viewModel.onAccountChange(bundle.getString(DialogChip.BUNDLE_KEY_ANSWER_NAME))
        }
    }

    fun goToImage() {
        getCurrentActivity().supportFragmentManager.replace(fragment = InstagramPlanerImageFragment.newInstance(), addToBackStack = true)
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