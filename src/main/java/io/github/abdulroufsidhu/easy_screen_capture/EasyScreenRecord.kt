package io.github.abdulroufsidhu.easy_screen_capture

import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Looper
import android.util.Log
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

private const val TAG = "easy-screen-recorder"

/**
 * @author <a href="https://github.com/abdulroufsidhu"> Abdul Rauf </a>
 * @since December 12, 2023,
 *
 *
 * EasyScreenRecord is a main safe library, purposed to ease the screen recording on top of android media projection and media projection manager
 *
 * > ### Usage Example
 * ```kotlin
 *
 * val screenRecorder = EasyScreenRecord(context)
 * // in Activity.onCreate
 * startActivityForResult(screenRecorder.permissionRequest, MY_REQUEST_CODE)
 * // in Activity.onActivityResult
 * val projector = screenRecorder.getProjector(resultCode, data)
 * CoroutineScope(Dispatchers.IO).launch {
 *  screenRecorder.capture(
 *    projector,
 *    Size( 1280, 1280), // output file resolution in pixels works best with 1:1
 *    storagePath + "fileName.png", // path where file shall be saved
 *    false, // flag to include or not audio in the screen recording can be either true or false
 *  )
 *  val timeInMillisToRecord = 30000 // 30 seconds
 *  delay(timeInMillisToRecord)
 *  screenRecorder.stop(projector) // below that line file shall be available at the provided location in screenRecorder.capture
 * }
 * ```
 */
class EasyScreenRecord(private val context: Context) {
	private val mProj =
		context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
	private val handler = android.os.Handler(Looper.getMainLooper())
	private var mediaRecorder: MediaRecorder? = null
	private val projectionCallback = EasyProjectorCallback {
		mediaRecorder?.stop()
		mediaRecorder?.reset()
		mediaRecorder?.release()
		mediaRecorder = null
	}
	
	/**
	 * purpose i to provide a screencast or capture permission request from the system.
	 */
	val permissionRequest = mProj.createScreenCaptureIntent()
	
	/**
	 * it must be called to fetch the [MediaProjection] instance on the permission result
	 */
	fun getProjector(permissionResultCode: Int, permissionResultData: Intent): MediaProjection =
		mProj.getMediaProjection(permissionResultCode, permissionResultData)
	
	suspend fun stop(projector: MediaProjection) = withContext(Dispatchers.Main) {
		projector.stop()
	}
	
	suspend fun capture(
		projector: MediaProjection,
		outputResolution: Size,
		fileOutputPath: String,
		recordAudio: Boolean,
	) = withContext(Dispatchers.IO) {
		try {
			val displayDensityPerInch = context.resources.displayMetrics.densityDpi
			initiateRecorder(
				outputResolution = outputResolution,
				fileOutputPath = fileOutputPath,
				recordAudio = recordAudio,
			)
			prepareRecorder()
			val flags =
				DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
			
			projector.registerCallback(
				projectionCallback,
				handler
			)
			val screencapName = fileOutputPath.split('/').last()
			
			withContext(Dispatchers.Main) {
				projector.createVirtualDisplay(
					screencapName, // display name
					outputResolution.width, // width
					outputResolution.height, // height
					displayDensityPerInch, // dpi
					flags, // display flags
					mediaRecorder?.surface, // display surface on the screen
					EasyVirtualDisplayCallback(), // callback
					handler,
				)
				mediaRecorder?.start()
			}
		} catch (e: Exception) {
			Log.w(TAG, "capture: $e", e)
		}
	}
	
	private suspend fun initiateRecorder(
		outputResolution: Size,
		fileOutputPath: String,
		recordAudio: Boolean,
	) = withContext(Dispatchers.IO) {
		val file = File(fileOutputPath)
		if (file.exists()) {
			file.delete()
		}
		file.createNewFile()
		if (mediaRecorder == null) {
			mediaRecorder = constructMediaRecorder()
		}
		if (recordAudio) {
			mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC);
		}
		mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE);
		mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		if(recordAudio) {
			mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		}
		val bit = 1
		val kBit = 1000 * bit
		val mBit = 1000 * kBit
		mediaRecorder?.setVideoEncodingBitRate(5 * mBit);
		mediaRecorder?.setVideoFrameRate(30);
		mediaRecorder?.setVideoSize(outputResolution.width, outputResolution.height);
		mediaRecorder?.setOutputFile(fileOutputPath);
	}
	
	private fun constructMediaRecorder() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		MediaRecorder(context)
	} else {
		MediaRecorder()
	}
	
	private suspend fun prepareRecorder() = withContext(Dispatchers.IO) {
		try {
			withContext(Dispatchers.Main) {
				mediaRecorder?.prepare()
			}
		} catch (e: IllegalStateException) {
			Log.w(TAG, "prepareRecorder: $e", e)
		} catch (e: IOException) {
			Log.w(TAG, "prepareRecorder: $e", e)
		}
	}
	
}