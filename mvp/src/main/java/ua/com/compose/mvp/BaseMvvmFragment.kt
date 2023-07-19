package ua.com.compose.mvp

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment

abstract class BaseMvvmFragment(private val layoutId: Int = 0): Fragment(layoutId), BaseMvpView {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseMvpView).setupBottomMenu(createBottomMenu())
    }
}