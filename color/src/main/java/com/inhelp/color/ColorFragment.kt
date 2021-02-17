package com.inhelp.color

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener
import getListener
import kotlinx.android.synthetic.main.fragment_color.*


class ColorFragment: BottomSheetDialogFragment() {

    interface ColorListener {
        fun onColorPick(color: Int) = Unit
    }

    companion object {

        private const val FRAGMENT_COLOR_INPUT_COLOR = "FRAGMENT_COLOR_INPUT_COLOR"

        fun newInstance(color: Int, targetFragment: Fragment) = ColorFragment().apply {
            this.setTargetFragment(targetFragment, 0)
            this.arguments = Bundle().apply {
                this.putInt(FRAGMENT_COLOR_INPUT_COLOR, color)
            }
        }
    }

    private var colorListener: ColorListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        colorListener = getListener(ColorListener::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_color, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getInt(FRAGMENT_COLOR_INPUT_COLOR)?.let { color ->
            colorPicker.setColor(color)
            imgExample.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }

        btnDone.setOnClickListener {
            colorListener?.onColorPick(color = imgExample.tag as Int)
            dismiss()
        }

        colorPicker.setColorSelectionListener(object : SimpleColorSelectionListener() {
            override fun onColorSelected(color: Int) {
                imgExample.tag = color
                imgExample.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
            }
        })
    }
}




//    companion object {
//        fun newInstance(): ColorFragment {
//            return ColorFragment()
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_color, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//    }
//}