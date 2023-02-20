package {seu_pacote}

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.annotation.NonNull
import androidx.core.content.FileProvider

import com.hbisoft.hbrecorder.HBRecorder
import com.hbisoft.hbrecorder.HBRecorderListener
import com.hbisoft.hbrecorder.HBRecorderVideoResult

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener

class HBRecorderPlugin : FlutterPlugin, MethodCallHandler, ActivityResultListener, HBRecorderListener {

  private lateinit var channel: MethodChannel
  private lateinit var registrar: PluginRegistry.Registrar
  private lateinit var hbRecorder: HBRecorder
  private lateinit var result: Result
  private lateinit var filePath: String

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "hb_recorder")
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    this.result = result
    when (call.method) {
      "startRecording" -> {
        startRecording()
      }
      "stopRecording" -> {
        stopRecording()
      }
      "getVideo" -> {
        getVideo()
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun startRecording() {
    try {
      val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
      val fileName = "HBRecorder_${timeStamp}.mp4"
      val file = File(folder, fileName)
      filePath = file.absolutePath
      val uri = FileProvider.getUriForFile(
        registrar.context(),
        registrar.context().applicationContext.packageName + ".provider",
        file
      )
      hbRecorder = HBRecorder(registrar.activity(), this)
      hbRecorder.setVideoOutputFile(uri)
      hbRecorder.setAudioBitrate(128000)
      hbRecorder.setAudioSamplingRate(44100)
      hbRecorder.setVideoBitrate(1000000)
      hbRecorder.isAudioEnabled = true
      hbRecorder.isAudioSourceEnabled = true
      hbRecorder.isAudioEncoderEnabled = true
      hbRecorder.isVideoEncoderEnabled = true
      hbRecorder.prepareMediaRecorder()
      hbRecorder.startScreenRecording()
      result.success(filePath)
    } catch (e: IOException) {
      e.printStackTrace()
      result.error("IOException", e.localizedMessage, null)
    } catch (e: Exception) {
      e.printStackTrace()
      result.error("Exception", e.localizedMessage, null)
    }
  }

  private fun stopRecording() {
    try {
      hbRecorder.stopScreenRecording()
      result.success(filePath)
    } catch (e: Exception) {
      e.printStackTrace()
      result.error("Exception", e.localizedMessage, null)
    }
  }

  private lateinit var result: MethodChannel.Result
  private lateinit var registrar: PluginRegistry.Registrar
  private lateinit var hbRecorder: HBRecorder
  private lateinit var filePath: String

  init {
    hbRecorder = HBRecorder(registrar.activity())
    hbRecorder.setOnHbRecorderListener(this)
  }

  fun startRecording(result: MethodChannel.Result) {
    this.result = result
    if (hbRecorder.isBusyRecording) {
      result.error("RecordingError", "HBRecorder is already busy recording", null)
      return
    }
    try {
      hbRecorder.startScreenRecording(registrar.activity(), HBRecorder.HBRecorderIntent(intent))
    } catch (e: Exception) {
      e.printStackTrace()
      result.error("Exception", e.localizedMessage, null)
    }
  }

  private fun getVideo() {
    try {
      val videoUri = Uri.parse(filePath)
      val intent = Intent(Intent.ACTION_VIEW, videoUri)
      intent.setDataAndType(videoUri, "video/mp4")
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      registrar.activity().startActivity(intent)
      result.success(null)
    } catch (e: Exception) {
      e.printStackTrace()
      result.error("Exception", e.localizedMessage, null)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (requestCode == HBRecorder.VIDEO_CAPTURE) {
      if (resultCode == Activity.RESULT_OK) {
        val videoFile: HBRecorderVideoResult? =
          data?.getParcelableExtra(HBRecorder.INTENT_VIDEO_FILE)
        if (videoFile != null) {
          filePath = videoFile.videoUri.toString()
          result.success(filePath)
        } else {
          result.error("VideoFileError", "Error getting video file", null)
        }
      } else {
        result.error("ActivityResultError", "Error capturing video", null)
      }
      return true
    }
    return false
  }

  override fun HBRecorderOnStart() {
    result.success(null)
  }

  override fun HBRecorderOnError(errorCode: Int, reason: String?) {
    result.error("HBRecorderError", "Error code: $errorCode, Reason: $reason", null)
  }

  override fun HBRecorderOnComplete() {
    result.success(filePath)
  }
}