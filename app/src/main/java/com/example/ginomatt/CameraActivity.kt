package com.example.ginomatt

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import java.util.concurrent.Executors
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

class CameraActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var tvCounter: TextView

    private var repCount = 0
    private var armStraight = true
    private var lastRepTime: Long = 0
    private val minRepIntervalMs = 700L

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.previewView)
        tvCounter = findViewById(R.id.tvCounter)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
        } else startCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview =
                Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val poseOptions = AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                .build()
            val poseDetector = PoseDetection.getClient(poseOptions)

            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(poseDetector, imageProxy)
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    analysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(
        poseDetector: com.google.mlkit.vision.pose.PoseDetector,
        imageProxy: ImageProxy
    ) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            poseDetector.process(inputImage)
                .addOnSuccessListener { pose ->
                    handleBicepCurl(pose)
                }
                .addOnCompleteListener { imageProxy.close() }
        } else imageProxy.close()
    }

    /** --------- SIMPLE BICEP CURL (ONE ARM) --------- */
    private fun handleBicepCurl(pose: Pose) {
        val required = listOf(
            PoseLandmark.RIGHT_SHOULDER,
            PoseLandmark.RIGHT_ELBOW,
            PoseLandmark.RIGHT_WRIST
        )
        val now = SystemClock.elapsedRealtime()
        if (required.any { pose.getPoseLandmark(it) == null }) return

        val shoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)!!
        val elbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)!!
        val wrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)!!

        val angle = angle(shoulder, elbow, wrist)
        Log.d("BicepCurl", "Angle: $angle")

        val armFullyStraight = angle > 160
        val armBent = angle < 60

        if (armFullyStraight) armStraight = true

        if (armBent && armStraight) {
            val timeSinceLastRep = now - lastRepTime
            if (timeSinceLastRep > minRepIntervalMs) {
                repCount++
                lastRepTime = now
                armStraight = false
                runOnUiThread { tvCounter.text = "Reps: $repCount" }
            }
        }
    }

    private fun angle(a: PoseLandmark, b: PoseLandmark, c: PoseLandmark): Double {
        val abx = a.position.x - b.position.x
        val aby = a.position.y - b.position.y
        val cbx = c.position.x - b.position.x
        val cby = c.position.y - b.position.y
        val dot = abx * cbx + aby * cby
        val mag1 = sqrt(abx.pow(2) + aby.pow(2))
        val mag2 = sqrt(cbx.pow(2) + cby.pow(2))
        if (mag1 == 0f || mag2 == 0f) return 0.0
        val cos = (dot / (mag1 * mag2)).coerceIn(-1f, 1f)
        return Math.toDegrees(acos(cos.toDouble()))
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Total Reps: $repCount", Toast.LENGTH_LONG).show()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
