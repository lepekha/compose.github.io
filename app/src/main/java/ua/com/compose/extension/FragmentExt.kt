package ua.com.compose.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun Fragment.hideKeyboard() {
    (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { inputMethodManager ->
        activity?.currentFocus?.windowToken?.let { inputMethodManager.hideSoftInputFromWindow(it, 0) }
    }
}

fun FragmentManager.replace(fragment: Fragment, containerId: Int, addToBackStack: Boolean = false) {
    val transaction = this.beginTransaction()
    transaction.replace(containerId, fragment, fragment.javaClass.simpleName)
    if (addToBackStack) {
        transaction.addToBackStack(fragment.javaClass.simpleName)
    }
    transaction.commit()
}

fun FragmentManager.remove(fragmentTag: String = "") {
    val fragment = this.findFragmentByTag(fragmentTag) ?: return
    val transaction = this.beginTransaction()
    transaction.remove(fragment)
    transaction.commit()
}

fun FragmentManager.back(): Boolean {
    if (this.backStackEntryCount > 1) {
        this.popBackStack()
        return true
    } else {
        return false
    }
}

fun FragmentManager.clearAllFragments(): Boolean {
    this.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    return true
}