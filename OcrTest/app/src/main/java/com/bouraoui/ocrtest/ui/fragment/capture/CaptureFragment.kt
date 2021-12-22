package com.bouraoui.ocrtest.ui.fragment.capture

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bouraoui.ocrtest.R
import com.bouraoui.ocrtest.data.model.ParagraphModel
import com.bouraoui.ocrtest.databinding.FragmentCaptureBinding
import com.bouraoui.ocrtest.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class CaptureFragment : Fragment() {

    private lateinit var binding: FragmentCaptureBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private lateinit var textOutputDirectory: File
    private val captureFragmentViewModel: CaptureFragmentViewModel by activityViewModels()
    private var inCameraMode = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCaptureBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showCameraPreview()
        openCamera()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        outputDirectory = MainActivity.getOutputDirectory(requireContext())
        textOutputDirectory = MainActivity.getTextFileOutputDirectory(requireContext())

        cameraExecutor = Executors.newSingleThreadExecutor()
        //val metrics = androidx.window.WindowManager(requireContext())
        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits our use cases
            //.setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .setTargetResolution(Size(1080, 1920))
            .build()

        registerCameraPermission()
        requestCameraPermission()
    }


    private fun registerCameraPermission() {
        requestCameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    Log.d(
                        "Capture Fragment",
                        "registerCameraPermission - Camera Permission Granted"
                    )
                    openCamera()
                } else {
                    Log.d(
                        "Capture Fragment",
                        "registerCameraPermission - Camera Permission NOT Granted"
                    )
                    requestCameraPermission()
                }
            }
    }


    private fun requestCameraPermission() {

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {

                Log.d("TAG", "requestCameraPermission - Camera Permission Granted")
                openCamera()

                // The permission is granted
                // you can go with the flow that requires permission here
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // This case means user previously denied the permission
                // So here we can display an explanation to the user
                // That why exactly we need this permission
                Log.d("TAG", "requestCameraPermission - Camera Permission NOT Granted")
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestCameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }


    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            setUpCameraUi()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e("TAG", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setUpCameraUi() {
        binding.takePhotoBtn.setOnClickListener {

            // Get a stable reference of the modifiable image capture use case
            imageCapture?.let { imageCapture ->

                // Create output file to hold the image
                val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

                // Setup image capture metadata

                // Create output options object which contains file + metadata
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                    .build()

                // Setup image capture listener which is triggered after photo has been taken
                imageCapture.takePicture(
                    outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                            Log.d(TAG, "Photo capture succeeded: $savedUri")
                            readDocument(photoFile)

                            // Implicit broadcasts will be ignored for devices running API level >= 24
                            // so if you only target API level 24+ you can remove this statement
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                requireActivity().sendBroadcast(
                                    Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri)
                                )
                            }

                            // If the folder selected is an external media directory, this is
                            // unnecessary but otherwise other apps will not be able to access our
                            // images unless we scan them using [MediaScannerConnection]
                            val mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(savedUri.toFile().extension)
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(savedUri.toFile().absolutePath),
                                arrayOf(mimeType)
                            ) { _, uri ->
                                Log.d(TAG, "Image capture scanned into media store: $uri")
                            }
                        }
                    })


            }
        }
    }

    private fun readDocument(photo: File) {
        lifecycleScope.launch {
            binding.takePhotoBtn.visibility = View.GONE
            binding.ocrInProgressTV.visibility = View.VISIBLE
            captureFragmentViewModel.readDocument(photo) { result, code ->
                when (code) {
                    200 -> {
                        binding.takePhotoBtn.visibility = View.VISIBLE
                        binding.ocrInProgressTV.visibility = View.GONE
                        if (result?.response?.paragraphs != null)
                            showTextPreview(result.response.paragraphs)
                    }
                    507 -> {
                        binding.takePhotoBtn.visibility = View.VISIBLE
                        binding.ocrInProgressTV.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error , No text found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        binding.takePhotoBtn.visibility = View.VISIBLE
                        binding.ocrInProgressTV.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error , please try again late!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun showTextPreview(paragraphList: MutableList<ParagraphModel>) {
        var string = ""
        paragraphList.forEach {
            string += it.paragraph.plus("\n")
        }
        binding.textPreviewTV.text = string
        binding.textPreviewTV.visibility = View.VISIBLE
        binding.scroll.visibility = View.VISIBLE
        binding.cardView.visibility = View.INVISIBLE
        binding.takePhotoBtn.text = getString(R.string.save_to_library_label)
        binding.takePhotoBtn.visibility = View.VISIBLE
        inCameraMode = false
        binding.takePhotoBtn.setOnClickListener {
            saveTextFile(string)
        }
    }

    private fun showCameraPreview() {
        binding.textPreviewTV.visibility = View.INVISIBLE
        binding.scroll.visibility = View.INVISIBLE
        binding.cardView.visibility = View.VISIBLE
        binding.takePhotoBtn.text = getString(R.string.capture_label)
        inCameraMode = true
        binding.takePhotoBtn.isEnabled = true
        binding.takePhotoBtn.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.purple_500))
    }

    private fun saveTextFile(data: String) {
        val txtFile = createFile(textOutputDirectory, TXT_FILENAME, TXT_EXTENSION)
        txtFile.parentFile?.mkdirs()
        txtFile.createNewFile()
        txtFile.writeText(data)
        disableButton()

    }

    private fun disableButton() {
        binding.takePhotoBtn.backgroundTintList = ColorStateList.valueOf(Color.GRAY)
        binding.takePhotoBtn.isEnabled = false
        captureFragmentViewModel.navigateLiveData.value = true
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            CaptureFragment()


        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val TXT_FILENAME = "dd-MM-yy_HH:mm:ss"
        private const val PHOTO_EXTENSION = ".png"
        private const val TXT_EXTENSION = ".txt"

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )
    }


}