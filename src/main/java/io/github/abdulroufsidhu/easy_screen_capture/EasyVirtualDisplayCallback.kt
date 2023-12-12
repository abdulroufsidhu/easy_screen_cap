package io.github.abdulroufsidhu.easy_screen_capture

import android.hardware.display.VirtualDisplay
import android.util.Log

private const val TAG = "easy-vd-callback"

class EasyVirtualDisplayCallback: VirtualDisplay.Callback() {
	override fun onPaused() {
		super.onPaused()
		Log.d(TAG, "onPaused: ")
	}
	
	override fun onResumed() {
		super.onResumed()
		Log.d(TAG, "onResumed: ")
	}
	
	override fun onStopped() {
		super.onStopped()
		Log.d(TAG, "onStopped: ")
	}
}