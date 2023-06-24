package ua.com.compose.mvp.data
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : ViewBinding> Fragment.viewBindingWithBinder(
        binder: (View) -> T
) = FragmentAutoClearedValueBinding(binder)

class FragmentAutoClearedValueBinding<T : ViewBinding>(
    val binder: (View) -> T
) : ReadOnlyProperty<Fragment, T>,
        DefaultLifecycleObserver {

    private var value: T? = null
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        value = null // Clear reference.
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return value ?: binder(thisRef.requireView()).also {
            setValue(thisRef, it)
        }
    }

    private fun setValue(fragment: Fragment, value: T) {
        fragment.viewLifecycleOwner.lifecycle.removeObserver(this)
        this.value = value
        fragment.viewLifecycleOwner.lifecycle.addObserver(this)
    }
}