package com.omanshuaman.testingtournamentsports

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.omanshuaman.testingtournamentsports.inventory.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class PosterZoom : AppCompatActivity() {

    private var imageView: PhotoView? = null
    private var download: FloatingActionButton? = null
    private val outputDir = "Android11Permissions"
    private var shareBtn: FloatingActionButton? = null
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poster_zoom)
        supportActionBar?.hide()

        imageView = findViewById(R.id.simpleImage)
        download = findViewById(R.id.download)
        shareBtn = findViewById(R.id.shareBtn)

        val loadingDialog = LoadingDialog(this@PosterZoom)

        val bundle = intent.extras
        val message = bundle!!.getString("backgroundImage")

        Glide.with(imageView!!).load(message)
            .apply(
                RequestOptions()
                    .fitCenter()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .override(Target.SIZE_ORIGINAL)
            )
            .into(imageView!!)

        shareBtn!!.setOnClickListener {
            loadingDialog.startLoadingDialog()

            Handler(Looper.getMainLooper()).postDelayed({
                // Your Code
                CoroutineScope(Dispatchers.IO).launch {

                    val bitmap: Bitmap =
                        withContext(Dispatchers.IO) {
                            Glide.with(this@PosterZoom)
                                .asBitmap()
                                .load(message).apply(
                                    RequestOptions()
                                        .fitCenter()
                                        .format(DecodeFormat.PREFER_ARGB_8888)
                                        .override(Target.SIZE_ORIGINAL)
                                )
                                .submit()
                                .get()
                        }
                    withContext(Dispatchers.Main) {
                        shareImageAndText(bitmap)
                        loadingDialog.dismissDialog()

                    }
                }
            }, 100)

        }

        download!!.setOnClickListener {
            loadingDialog.startLoadingDialog()

            Handler(Looper.getMainLooper()).postDelayed({
                CoroutineScope(Dispatchers.IO).launch {

                    val bitmap: Bitmap =
                        withContext(Dispatchers.IO) {
                            Glide.with(this@PosterZoom)
                                .asBitmap()
                                .load(message).apply(
                                    RequestOptions()
                                        .fitCenter()
                                        .format(DecodeFormat.PREFER_ARGB_8888)
                                        .override(Target.SIZE_ORIGINAL)
                                )
                                .submit()
                                .get()
                        }
                    withContext(Dispatchers.IO) {

                        var outStream: FileOutputStream? = null
                        val path = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        )
                        val file = File(path, "DemoPicture.jpg")
                        path.mkdirs()
                        outStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                        outStream.flush()
                        outStream.close()

                        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        intent.data = Uri.fromFile(file)
                        sendBroadcast(intent)
                        loadingDialog.dismissDialog()

                    }
                }

            }, 100)

        }
    }

    private fun shareImageAndText(bitmap: Bitmap) {

        //first we will save this image in cache, get the saved image uri
        val uri: Uri = saveImageToShare(bitmap)

        //share intent
        val sIntent = Intent(Intent.ACTION_SEND)
        sIntent.putExtra(Intent.EXTRA_STREAM, uri)
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        sIntent.type = "image/png"

        val chooser = Intent.createChooser(sIntent, "Share File")

        val resInfoList =
            this.packageManager.queryIntentActivities(
                chooser,
                PackageManager.MATCH_DEFAULT_ONLY
            )

        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        startActivity(chooser)


    }

    private fun saveImageToShare(bitmap: Bitmap): Uri {
        val imageFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imageFolder.mkdirs() //create if not exists
            val file = File(imageFolder, "shared_image.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(
                this,
                "com.omanshuaman.testingtournamentsports.fileprovider",
                file
            )
        } catch (e: Exception) {
            Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
        }
        return uri!!
    }
}
