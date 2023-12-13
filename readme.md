# EasyScreenCapture Library
## Overview


**Author:** [Abdul Rauf](https://www.github.com/abdulroufsidhu)

**Version:** 0.1-alpha

**Release Date:** December 12, 2023

The EasyScreenCapture library is a powerful yet simple-to-use Android library that facilitates screen recording and screenshot capturing using Android's media projection and media projection manager. This library is designed to streamline the process of capturing the screen, offering flexibility in settings such as resolution, file path, and audio inclusion for screen recording.
### Key Features
#### [Screen Recording](#EasyScreenRecord)

   - Simplified Initialization: Initialize the library with a single line of code, making it easy to integrate into your Android application.

   - Permission Handling: Seamlessly request and handle screen capture permissions using the provided permissionRequest property.

   - Media Projection Management: Obtain the MediaProjection instance with the getProjector method, simplifying the integration of screen recording functionality.

   - Configurable Capture Settings: Specify output resolution, file path, and audio inclusion settings with ease using the capture method.

   - Background Execution: Perform screen recording in the background using coroutines, allowing your application to remain responsive.

#### [Screenshot Capturing](#easyscreenshot)

   - Convenient Setup: The library follows a similar pattern for screenshot capturing, making it intuitive for developers familiar with the screen recording functionality.

   - Callback Support: Provide a callback function through the onFileSaved parameter in the capture method to execute custom actions when a screenshot is saved.

   - Image Handling: Automatically handle the acquisition, processing, and saving of screenshots, ensuring a smooth and hassle-free experience.

   - Resource Management: The library takes care of releasing resources and stopping the media projection after completing the screenshot capture process.

### Usage Example
 - [Screen Recording](#usage-example-1)
 - [Screenshot Capturing](#usage-example-2)


### Implementation Details

   - `**Dependencies**`: The library relies on the Android `**MediaProjectionManager**` and `**MediaProjection**` for screen capture.

   - `**Threading**`: The library efficiently manages threading, executing background tasks on the IO dispatcher and interacting with UI elements on the Main dispatcher.

   - `**Resource Cleanup**`: Automatic release of resources and stopping of media projection after completing screen recording or screenshot capture.

   EasyScreenCapture simplifies the implementation of screen recording and screenshot capturing, making it a valuable addition to your Android development toolkit.

## EasyScreenRecord
### Introduction

**Author:** [Abdul Rauf](https://www.github.com/abdulroufsidhu)

**Version:** 0.1-alpha

**Release Date:** December 12, 2023

EasyScreenRecord is a lightweight and user-friendly Android library designed to simplify screen recording using Android's media projection and media projection manager. This library provides an easy-to-use interface for capturing the screen with or without audio and allows you to specify output resolution and file path.
### Usage Example

```kotlin

val screenRecorder = EasyScreenRecord(context)

// Request screen capture permission
startActivityForResult(screenRecorder.permissionRequest, MY_REQUEST_CODE)

// Handle permission result in Activity.onActivityResult
val projector = screenRecorder.getProjector(resultCode, data)

// Capture the screen with specified settings
CoroutineScope(Dispatchers.IO).launch {
screenRecorder.capture(
projector,
Size(1280, 1280), // Output file resolution in pixels (1:1 aspect ratio recommended)
storagePath + "fileName.png", // Path where the file shall be saved
false // Include audio in the screen recording (true or false)
)

    val timeInMillisToRecord = 30000 // 30 seconds
    delay(timeInMillisToRecord)

    // Stop screen recording
    screenRecorder.stop(projector)
}
```
### API Documentation
#### `EasyScreenRecord` Class
Constructor

   - **`EasyScreenRecord(context: Context):`** Initializes the EasyScreenRecord instance with the provided **`Context`**.

Properties

   - **`permissionRequest:`** Use this property to request screen capture permission. It returns an **`Intent`** that should be started with **`startActivityForResult`**.

Methods

   - **`getProjector(permissionResultCode: Int, permissionResultData: Intent): MediaProjection:`** Must be called in **`onActivityResult`** to obtain the **`MediaProjection`** instance.

   - **`capture(projector: MediaProjection, outputResolution: Size, fileOutputPath: String, recordAudio: Boolean)`**: Captures the screen with the specified settings.

   - **`stop(projector: MediaProjection)`**: Stops the screen recording.

#### Implementation Details
##### Dependencies

   - The library relies on the Android **`MediaProjectionManager`** and **`MediaProjection`** for screen capture.

#### Important Notes

   - The **`capture`** method runs on the IO dispatcher, while the **`stop`** method runs on the Main dispatcher to interact with UI elements.

   - Screen recording is done in the background using a coroutine.

   - The library automatically handles the release of resources after screen recording is completed.

## EasyScreenshot
### Introduction

**Author:** [Abdul Rauf](https://www.github.com/abdulroufsidhu)

**Version:** 0.1-alpha

**Release Date:** December 12, 2023

EasyScreenshot is a lightweight Android library designed to simplify screen capturing using Android's media projection and media projection manager. This library provides an easy-to-use interface for capturing screenshots and saving them to a specified file path.
### Usage Example

```kotlin

val screenshot = EasyScreenshot(context)

// Request screen capture permission
startActivityForResult(screenshot.permissionRequest, MY_REQUEST_CODE)

// Handle permission result in Activity.onActivityResult
val projector = screenshot.getProjector(resultCode, data)

// Capture a screenshot with specified settings
CoroutineScope(Dispatchers.IO).launch {
    screenshot.capture(
        projector,
        Size(1280, 1280), // Output file resolution in pixels (1:1 aspect ratio recommended)
        storagePath + "fileName.png", // Path where the file shall be saved
        {
          Log.i(TAG, "onFileSaved: file has been saved")
        } // onFileSaved lambda function with no parameters
    )
}
```
### API Documentation
#### `EasyScreenshot` Class
Constructor

   - **`EasyScreenshot(context: Context):`** Initializes the EasyScreenshot instance with the provided **`Context`**.

Properties

   - **`permissionRequest`**: Use this property to request screen capture permission. It returns an **`Intent`** that should be started with **`startActivityForResult`**.

Methods

   - **`getProjector(permissionResultCode: Int, permissionResultData: Intent): MediaProjection`**: Must be called in **`onActivityResult`** to obtain the **`MediaProjection`** instance.

   - **`capture(projector: MediaProjection, outputResolution: Size, fileOutputPath: String, onFileSaved: () -> Unit)`**: Captures a screenshot with the specified settings and invokes the **`onFileSaved`** lambda function when the file is saved.

#### Implementation Details
##### Dependencies

   - The library relies on the Android **`MediaProjectionManager`** and **`MediaProjection`** for screen capture.

### Important Notes

   - The **`capture`** method runs on the IO dispatcher.

   - Screenshot capturing is done in the background using a coroutine.

   - The library automatically handles the release of resources after capturing the screenshot.