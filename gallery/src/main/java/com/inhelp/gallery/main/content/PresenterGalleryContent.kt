package com.inhelp.gallery.main.content

import android.content.Context
import android.net.Uri
import com.inhelp.base.mvp.BaseMvpPresenterImpl
import com.inhelp.base.mvp.bottomSheetFragment.BaseMvpDialogPresenterImpl
import com.inhelp.gallery.main.PresenterGallery

class PresenterGalleryContent(val context: Context, val presenterGallery: PresenterGallery) : BaseMvpDialogPresenterImpl<ViewGalleryContent>() {

    val images = mutableListOf<Uri>()
    val selectedImages = presenterGallery.selectedImages
    var isMultiSelect = presenterGallery.isMultiSelect

    fun init(position: Int){
        images.clear()
        images.addAll(presenterGallery.folders[position].images)
    }

    fun pressImage(uri: Uri, isMultiSelect: Boolean){
        presenterGallery.isMultiSelect = isMultiSelect or presenterGallery.isMultiSelect
        if(selectedImages.contains(uri)){
            selectedImages.remove(uri)
            if(selectedImages.isEmpty()){
                presenterGallery.isMultiSelect = false
            }
        }else{
            selectedImages.add(uri)
        }
        presenterGallery.updateCounter()

        if(!presenterGallery.isMultiSelect && selectedImages.isNotEmpty()){
            presenterGallery.addImage()
        }
    }
}
