package com.inhelp.view.main.main

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inhelp.R
import com.inhelp.core.models.data.Menu
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import android.content.Intent
import com.inhelp.core.LogConsoleService
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.crop.view.main.CropFragment
import com.inhelp.gallery.main.FragmentGallery
import com.inhelp.grids.main.GridFragment
import com.inhelp.panorama.view.main.PanoramaFragment
import com.inhelp.tags.main.FragmentTags
import com.inhelp.text_style.main.TextStyleFragment
import replace


class FragmentMain : BaseMvpFragment<ViewMain, PresenterMain>(), ViewMain {

    private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084

    override val presenter: PresenterMain by inject()

    private lateinit var mMenuRvAdapter: MenuRvAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_menu))
        initList()
        presenter.updateList()
    }

    private fun initList() {
        menuList.layoutManager = GridLayoutManager(getCurrentContext(), 2)
        mMenuRvAdapter = MenuRvAdapter(presenter.menuList,
                clickListener = { menuItem ->
                    when (menuItem) {
                        Menu.MENU_TEXT_STYLE -> {
                            getCurrentActivity().supportFragmentManager.replace(fragment = TextStyleFragment.newInstance(), addToBackStack = true)
                        }
                        Menu.MENU_TAGS -> {
                            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentTags.newInstance(), addToBackStack = true)
                        }
//                        Menu.MENU_GALLERY -> {
//                            getCurrentActivity().supportFragmentManager.replace(fragment = FragmentGallery.newInstance(targetFragment = this), addToBackStack = true)
//                        }
                        Menu.MENU_CROP -> {
                            getCurrentActivity().supportFragmentManager.replace(fragment = CropFragment.newInstance(), addToBackStack = true)
                        }
                        Menu.MENU_PANORAMA -> {
                            getCurrentActivity().supportFragmentManager.replace(fragment = PanoramaFragment.newInstance(), addToBackStack = true)
                        }
                        Menu.MENU_GRID -> {
                            getCurrentActivity().supportFragmentManager.replace(fragment = GridFragment.newInstance(), addToBackStack = true)
                        }
//                        Menu.MENU_SAVE_PHOTO -> presenter.pressSave()
//                        Menu.MENU_TAGS -> {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(getCurrentContext())) {
//
//
//                                If the draw over permission is not available open the settings screen
//                                to grant the permission.
//                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                                        Uri.parse("package:" + getCurrentContext().packageName))
//                                startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
//                            } else {
//                                getCurrentContext().startService(Intent(getCurrentContext(), LogConsoleService::class.java))
//                                activity?.finish()
//                            }
//                            presenter.pressTags()
//                        }
                    }
                })
        menuList?.adapter = mMenuRvAdapter
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                getCurrentContext().startService(Intent(getCurrentContext(), LogConsoleService::class.java))
                activity?.finish()
            } else { //Permission is not available
                Toast.makeText(getCurrentContext(),
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show()

                activity?.finish()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun updatePhotoList(){
    }
}