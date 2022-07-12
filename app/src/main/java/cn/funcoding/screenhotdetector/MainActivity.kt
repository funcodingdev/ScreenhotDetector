package cn.funcoding.screenhotdetector

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

private const val REQUEST_CODE = 1000

class MainActivity : AppCompatActivity() {
    private var screenshotDetector: ScreenshotDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        screenshotDetector = ScreenshotDetector(applicationContext)
        screenshotDetector?.setOnDataChangeListener { uri ->
            Toast.makeText(this, "${uri.parseToPath(this)}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startDetector()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        }
    }

    private fun startDetector() {
        screenshotDetector?.start()
    }

    private fun stopDetector() {
        screenshotDetector?.stop()
    }

    override fun onStop() {
        super.onStop()
        stopDetector()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDetector()
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}