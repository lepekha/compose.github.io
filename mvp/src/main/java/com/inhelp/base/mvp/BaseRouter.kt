package com.inhelp.base.mvp

import androidx.annotation.NonNull


abstract class BaseRouter(private val fragmentManager: androidx.fragment.app.FragmentManager) {
    abstract fun showStartScreen()

    protected var currentStep: BaseMvpView? = null

    fun navigateTo(@NonNull fragment: androidx.fragment.app.Fragment, container: Int, addToBackStack: Boolean = true) {
        checkNotNull(fragmentManager)
        checkNotNull(fragment)
        val transaction = fragmentManager.beginTransaction()
//        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction.replace(container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(fragment.tag)
        }
        transaction.commit()
    }


    fun clearBackStack() {
        val fragmentManager = fragmentManager
        while (fragmentManager.backStackEntryCount != 0) {
            fragmentManager.popBackStackImmediate()
        }
    }

    fun back() {
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        }
    }

}