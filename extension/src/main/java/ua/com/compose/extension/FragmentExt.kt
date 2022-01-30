package ua.com.compose.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() {
    (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { inputMethodManager ->
        activity?.currentFocus?.windowToken?.let { inputMethodManager.hideSoftInputFromWindow(it, 0) }
    }
}