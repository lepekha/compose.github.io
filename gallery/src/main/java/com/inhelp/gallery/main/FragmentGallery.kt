package com.inhelp.gallery.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.dsl.PermissionManager
import com.eazypermissions.dsl.extension.requestPermissions
import com.inhelp.base.mvp.BaseMvpFragment
import com.inhelp.gallery.R
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named


class FragmentGallery : BaseMvpFragment<ViewGallery, PresenterGallery>(), ViewGallery {

    companion object {
        private const val REQUEST_PERMISSIONS = 101
        const val ARGUMENT_ONE_URI = "ARGUMENT_ONE_URI"
        const val ARGUMENT_OPEN_GALLARY = "ARGUMENT_OPEN_GALLARY"

        fun newInstance(targetFragment: Fragment): FragmentGallery {
            return FragmentGallery().apply {
                this.setTargetFragment(targetFragment, 0)
            }
        }
    }

    override val presenter: PresenterGallery by lazy {
        val scope = getKoin().getOrCreateScope(
                "gallery", named("gallery"))
        scope.get()
    }

    private val adapter by lazy { GalleryContentRvAdapter(this.childFragmentManager, presenter.folders) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    private fun initList() {
        list.post {
            list.offscreenPageLimit = 1
            list.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getCurrentContext().getString(R.string.fragment_title_gallery))
//        checkPermission()

        PermissionManager.requestPermissions(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ){
            requestCode = 4
            resultCallback = {
                when(this) {
                    is PermissionResult.PermissionGranted -> {
                        presenter.getAllShownImagesPath(getCurrentActivity())
                        initList()
                    }
                    is PermissionResult.PermissionDenied -> {
                        backToMain()
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                        backToMain()
                    }
                    is PermissionResult.ShowRational -> {
                        //If user denied permission frequently then she/he is not clear about why you are asking this permission.
                        //This is your chance to explain them why you need permission.
                    }
                }
            }
        }

        this.targetFragment?.arguments = Bundle().apply {
            this.putBoolean(ARGUMENT_OPEN_GALLARY, true)
        }
    }

    override fun setVisibleTabs(isVisible: Boolean) {
        tab_layout.isVisible = isVisible
    }

    override fun passData(value: String) {
        this.targetFragment?.arguments = Bundle().apply {
            this.putString(ARGUMENT_ONE_URI, value)
        }
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(getCurrentContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getCurrentContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getCurrentActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(getCurrentActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(getCurrentActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_PERMISSIONS)
                ActivityCompat.requestPermissions(getCurrentActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_PERMISSIONS)
            }
        } else {
            presenter.getAllShownImagesPath(getCurrentActivity())
            initList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getKoin().getScope("gallery").close()
    }

    override fun updateAllList(){
        list.adapter?.notifyDataSetChanged()
    }

}