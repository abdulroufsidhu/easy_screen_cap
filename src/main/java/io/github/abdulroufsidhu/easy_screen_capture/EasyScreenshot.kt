package io.github.abdulroufsidhu.easy_screen_capture

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Looper
import android.util.Log
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

private const val TAG = "easy-screenshot"


/**
 * @author <a href="https://github.com/abdulroufsidhu"> Abdul Rauf </a>
 * @since December 12, 2023
 * EasyScreenshot is a main safe library,  purposed to ease the screen recording on top of android media projection and media projection manager
 *
 * > ### Usage Example
 * ```kotlin
 *
 * val screenshot = EasyScreenshot(context)
 * // in Activity.onCreate
 * startActivityForResult(screenshot.permissionRequest, MY_REQUEST_CODE)
 * // in Activity.onActivityResult
 * val projector = screenshot.getProjector(resultCode, data)
 * CoroutineScope(Dispatchers.IO).launch {
 *  screenshot.capture(
 *    projector,
 *    Size( 1280, 1280), // output file resolution in pixels works best with 1:1
 *    storagePath + "fileName.png", // path where file shall be saved
 *    {
 *      Log.i(TAG, "onFileSaved: file has been saved");
 *    }, // onFileSaved() lambda function with no parameters
 *  )
 * }
 * ```
 */
class EasyScreenshot(private val context: Context) {
	private val mProj =
		context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
	private val MAX_IMAGES = 2
	private val handler = android.os.Handler(Looper.getMainLooper())
	private val projectionCallback = EasyProjectorCallback{}
	
	
	/**
	 * purpose i to provide a screencast or capture permission request from the system.
	 */
	val permissionRequest = mProj.createScreenCaptureIntent()
	
	/**
	 * it must be called to fetch the [MediaProjection] instance on the permission result
	 */
	fun getProjector(permissionResultCode: Int, permissionResultData: Intent): MediaProjection =
		mProj.getMediaProjection(permissionResultCode, permissionResultData)
	
	suspend fun capture(
		projector: MediaProjection,
		outputResolution: Size,
		fileOutputPath: String,
		onFileSaved: ()->Unit,
	) = withContext(Dispatchers.IO) {
		val flags =
			DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
		val displayDensityPerInch = context.resources.displayMetrics.densityDpi
		val screencapName = fileOutputPath.split('/').last()
		val imageReader = ImageReader.newInstance(
			outputResolution.width,
			outputResolution.height,
			ImageFormat.JPEG,
			MAX_IMAGES
		)
		withContext(Dispatchers.Main) {
			projector.registerCallback(
				projectionCallback,
				handler
			)
			projector.createVirtualDisplay(
				screencapName, // display name
				outputResolution.width, // width
				outputResolution.height, // height
				displayDensityPerInch, // dpi
				flags, // display flags
				imageReader.surface, // display surface on the screen
				EasyVirtualDisplayCallback(), // callback
				handler,
			)
			imageReader.setOnImageAvailableListener({ reader ->
				try {
					reader.acquireLatestImage()?.let { image ->
						val imageBuffer = image.planes.first().buffer.rewind()
						val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
						bitmap.copyPixelsFromBuffer(imageBuffer)
						val fos = FileOutputStream(fileOutputPath)
						bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
						fos.close()
						bitmap.recycle()
						onFileSaved()
					}
				} catch (e: Throwable) {
					Log.w(TAG, "captureScreenshot: $e", e)
				} finally {
					reader.close()
					projector.stop()
					projector.unregisterCallback(projectionCallback)
				}
			}, handler)
		}
	}
}