package io.github.abdulroufsidhu.easy_screen_capture

import android.media.projection.MediaProjection

class EasyProjectorCallback(private val paramOnStop: ()-> Unit) : MediaProjection.Callback() {
	override fun onStop() {
		super.onStop()
		paramOnStop()
	}
}