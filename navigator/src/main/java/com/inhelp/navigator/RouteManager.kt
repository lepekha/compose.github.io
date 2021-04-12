import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.inhelp.navigator.R
import com.inhelp.navigator.SharedElement


fun Fragment.hideKeyboard() {
    (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { inputMethodManager ->
        activity?.currentFocus?.windowToken?.let { inputMethodManager.hideSoftInputFromWindow(it, 0) }
    }
}

fun Fragment.showKeyboard(view: View) {
    if (view.requestFocus()) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }
}

fun FragmentManager.loadFragment(containerId: Int = R.id.id_fragment_container, fragment: Fragment) {
    this.beginTransaction().add(containerId, fragment, fragment.javaClass.simpleName).commit()
}

fun FragmentManager.loadNowFragment(containerId: Int = R.id.id_fragment_container, fragment: Fragment) {
    this.beginTransaction().add(containerId, fragment, fragment.javaClass.simpleName).commitNow()
}

fun FragmentManager.hideFragment(fragment: Fragment) {
    this.beginTransaction().hide(fragment).commit()
}

fun FragmentManager.showFragment(fragment: Fragment) {
    this.beginTransaction().show(fragment).commit()
}

fun <T> Fragment.getListenerOrThrowException(listenerClazz: Class<T>): T {
    return getListener(listenerClazz)
            ?: throw IllegalStateException("Not found " + listenerClazz.simpleName)
}

fun <T> Fragment.getListener(listenerClazz: Class<T>): T? {
    var listener = getListenerFromTargetFragment(listenerClazz)
    if (listener != null) {
        return listener
    }

    listener = getListenerFromParentFragment(listenerClazz)
    if (listener != null) {
        return listener
    }

    listener = getListenerFromActivity(listenerClazz)
    return listener
}

private fun <T> getListener(listenerClass: Class<T>, target: Any?): T? {
    return if (listenerClass.isInstance(target)) {
        listenerClass.cast(target)
    } else {
        null
    }
}

fun <T> Fragment.getListenerFromTargetFragment(listenerClazz: Class<T>): T? {
    return getListener(listenerClazz, targetFragment)
}

fun <T> Fragment.getListenerFromParentFragment(listenerClazz: Class<T>): T? {
    return getListener(listenerClazz, parentFragment)
}

fun <T> Fragment.getListenerFromActivity(listenerClazz: Class<T>): T? {
    return getListener(listenerClazz, activity)
}

fun FragmentManager.replace(fragment: Fragment, containerId: Int = R.id.id_fragment_container, addToBackStack: Boolean = false, reordering: Boolean = false, vararg sharedElements: SharedElement) {
    val transaction = this.beginTransaction()
    sharedElements.iterator().forEach {
        transaction.addSharedElement(it.view, it.id)
    }
    transaction.setCustomAnimations(
            com.inhelp.theme.R.anim.fragment_enter,
            com.inhelp.theme.R.anim.fragment_exit,
            com.inhelp.theme.R.anim.fragment_enter,
            com.inhelp.theme.R.anim.fragment_exit
    )

    transaction.setReorderingAllowed(reordering)
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

