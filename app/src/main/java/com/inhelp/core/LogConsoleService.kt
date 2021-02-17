package com.inhelp.core

import android.app.Service
import android.view.WindowManager
import android.view.Gravity
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.content.Intent
import android.os.IBinder
import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.inhelp.R


class LogConsoleService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    private var isVisibleList = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.log_console, null)
        //Add the view to the window.
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)

        //Specify the view position
        params.gravity = Gravity.TOP or Gravity.LEFT        //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        //Add the view to the window
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        mWindowManager?.addView(mFloatingView, params)
//
        mFloatingView?.findViewById<ImageButton>(R.id.btnLogConsole)?.setOnClickListener {
            mFloatingView?.findViewById<RecyclerView>(R.id.logList)?.visibility = if (isVisibleList) {
                View.GONE
            } else {
                View.VISIBLE
            }
            isVisibleList = !isVisibleList
        }

        mFloatingView?.findViewById<ImageButton>(R.id.btnLogConsole)?.setOnLongClickListener {
            stopSelf()
            false
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) mWindowManager?.removeView(mFloatingView)
    }
}