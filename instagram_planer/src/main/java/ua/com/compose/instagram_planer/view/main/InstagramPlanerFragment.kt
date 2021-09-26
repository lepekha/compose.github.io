package ua.com.compose.instagram_planer.view.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.module_instagram_planer_fragment_instagram_planer.*
import kotlinx.android.synthetic.main.module_instagram_planer_fragment_instagram_planer.chipGroup
import ua.com.compose.dialog.dialogs.DialogColor
import ua.com.compose.instagram_planer.di.Scope
import ua.com.compose.instagram_planer.view.image.InstagramPlanerImageFragment
import ua.com.compose.dialog.dialogs.DialogConfirmation
import ua.com.compose.dialog.dialogs.DialogInput
import ua.com.compose.extension.*
import ua.com.compose.gallery.main.FragmentGallery
import ua.com.compose.mvp.data.BottomMenu
import ua.com.compose.mvp.data.Menu
import ua.com.compose.navigator.replace
import ua.com.compose.instagram_planer.R
import ua.com.compose.mvp.BaseMvvmFragment


class InstagramPlanerFragment: BaseMvvmFragment() {

    companion object {

        private const val REQUEST_CHANGE_IMAGE = "REQUEST_CHANGE_IMAGE"
        private const val REQUEST_DIALOG_COLOR = "REQUEST_DIALOG_COLOR"

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
        val request = DialogColor.show(fm = getCurrentActivity().supportFragmentManager, color = Color.GREEN)
        setFragmentResultListener(request) { requestKey, bundle ->
            viewModel.pressAddBox(color = bundle.getInt(DialogColor.BUNDLE_KEY_ANSWER_COLOR))
            clearFragmentResultListener(requestKey)
        }
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.module_instagram_palaner_title))

        initGridPreview()

        viewModel.loadUsers()

        viewModel.goToImage.observe(viewLifecycleOwner){
            it?.let {
                getCurrentActivity().supportFragmentManager.replace(fragment = InstagramPlanerImageFragment.newInstance(imageId = it.id), addToBackStack = true)
            }
        }

        setFragmentResultListener(FragmentGallery.REQUEST_KEY) { _, bundle ->
            viewModel.onAddImages((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        setFragmentResultListener(REQUEST_CHANGE_IMAGE) { _, bundle ->
            viewModel.pressChangeImage((bundle.getSerializable(FragmentGallery.BUNDLE_KEY_IMAGES) as List<*>).filterIsInstance<Uri>())
        }

        viewModel.changeImageInList.observe(viewLifecycleOwner){
            gridList.adapter?.notifyItemChanged(it, InstagramPlanerRvAdapter.CHANGE_ITEM_POSITION)
        }

        viewModel.images.observe(viewLifecycleOwner){
            (gridList.adapter as InstagramPlanerRvAdapter).updateImages(images = it)
        }

        viewModel.addImages.observe(viewLifecycleOwner){
            (gridList.adapter as InstagramPlanerRvAdapter).addImages(images = it)
        }

        viewModel.changeImages.observe(viewLifecycleOwner){
            (gridList.adapter as InstagramPlanerRvAdapter).changeImages(images = it)
        }

        viewModel.isVisiblePlaceHolder.observe(viewLifecycleOwner){
            imgPlaceholder.isVisible = it
        }

        viewModel.userName.observe(viewLifecycleOwner){
            txtAccount.text = it
        }

        viewModel.isVisibleClearAll.observe(viewLifecycleOwner){
            btnCopy.isVisible = it
        }

        viewModel.isVisibleMore.observe(viewLifecycleOwner){
            txtAccount.isVisible = it
            btnMore.isVisible = it
            btnAddFirstUser.isVisible = !it
            txtCreateFirstUser.isVisible = !it
            materialCardView.isClickable = it
            materialCardView.isEnabled = it
            setVisibleMore(false)
        }

        viewModel.createDialogInputName.observe(viewLifecycleOwner){
            createDialogInputName(text = it)
        }

        viewModel.createAlert.observe(viewLifecycleOwner){
            it?.let { showAlert(it) }
        }

        viewModel.createUsersList.observe(viewLifecycleOwner){
            showUsers(it)
        }

        viewModel.removeImage.observe(viewLifecycleOwner){
            (gridList.adapter as InstagramPlanerRvAdapter).removeImage(image = it)
        }

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
                    viewModel.pressRemoveImage(position = dragEvent.clipData.getItemAt(0).text.toString().toInt())
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
                    viewModel.pressImageSave(position = dragEvent.clipData.getItemAt(0).text.toString().toInt())
                }
            }
            true
        }

        btnChangeImage.setOnDragListener { _, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_ENTERED -> {
                    requireContext().vibrate(type = EVibrate.BUTTON)
                    btnChangeImage.animateScale(toScale = 1.5f)
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    btnChangeImage.animateScale(toScale = 1f)
                }
                DragEvent.ACTION_DROP -> {
                    btnChangeImage.animateScale(toScale = 1f)
                    groupButton.isVisible = false
                    viewModel.onChangeImage(position = dragEvent.clipData.getItemAt(0).text.toString().toInt())
                    FragmentGallery.show(fm = getCurrentActivity().supportFragmentManager, isMultiSelect = true, requestKey = REQUEST_CHANGE_IMAGE)
                }
            }
            true
        }

        btnMore.setVibrate(EVibrate.BUTTON)
        btnMore.setOnClickListener {
            setVisibleMore(!containerMore.isVisible)
        }

        materialCardView.setVibrate(EVibrate.BUTTON)
        materialCardView.setOnClickListener {
            setVisibleMore(!containerMore.isVisible)
        }

        btnAddUser.setVibrate(EVibrate.BUTTON)
        btnAddUser.setOnClickListener {
            viewModel.pressAddUser()
        }

        btnAddFirstUser.setVibrate(EVibrate.BUTTON)
        btnAddFirstUser.setOnClickListener {
            viewModel.pressAddUser()
        }

        btnCopy.setVibrate(EVibrate.BUTTON)
        btnCopy.setOnClickListener {
            val request = DialogConfirmation.show(fm = requireActivity().supportFragmentManager, message = requireContext().getString(R.string.module_instagram_palaner_clear_account))
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

        gridList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                setVisibleMore(false)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun setVisibleMore(isVisible: Boolean){
        containerMore.isVisible = isVisible
        if(containerMore.isVisible){
            imgMore.setImageResource(R.drawable.ic_expand_less)
        }else{
            imgMore.setImageResource(R.drawable.ic_expand_more)
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

    private fun createDialogInputName(text: String?){
        text ?: return
        val request = DialogInput.show(fm = getCurrentActivity().supportFragmentManager, text = text, singleLine = true)
        setFragmentResultListener(request) { _, bundle ->
            viewModel.onInputName(bundle.getString(DialogInput.BUNDLE_KEY_INPUT_MESSAGE))
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