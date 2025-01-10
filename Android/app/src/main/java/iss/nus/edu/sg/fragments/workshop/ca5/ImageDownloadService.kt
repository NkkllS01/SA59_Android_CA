package iss.nus.edu.sg.fragments.workshop.ca5

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageDownloadService : Service() {

    companion object {
        const val BROADCAST_ACTION = "com.example.memorygame.IMAGE_DOWNLOADED"
        const val EXTRA_IMAGE_PATH = "imagePath"
        const val EXTRA_PROGRESS = "progress"
        const val EXTRA_TOTAL = "total"

        fun clearDirectory(directory : File) {
            if (directory.exists() && directory.isDirectory) {
                directory.listFiles()?.forEach { it.delete() }
                Log.i(
                    "ImageDownloadService",
                    "Cleared old images from directory: ${directory.absolutePath}"
                )
            }
        }
    }

    private var currentUrl : String? = null         // Track the current URL
    private var isDownloading = false               // Flag to track the download status
    private var currentDownloadJob : Job? = null    // Job for controlling the download
        set(value) {
            field = value
            Log.d("ImageDownloadService", "Job updated: $field")
        }
    private var imageUrls = mutableListOf<String>() // Holds the current image URLs

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("ImageDownloadActivity", "Intent Action: ${intent?.action}")
        when (intent?.action) {
            "download" -> {
                Log.d("ImageDownloadService", "currentDownloadJob: $currentDownloadJob, isDownloading: $isDownloading")
                val url = intent.getStringExtra("url")
                if (!url.isNullOrEmpty()) {
                    // Stop any existing download thread
                    if (url != currentUrl) {
                        stopCurrentDownload()
                        // Clear previous image URLs before starting new download
                        imageUrls.clear()
                        Log.i("ImageDownloadService", "Image URLs cleared. Starting download for $url...")

                        // Set the new URL as the current URL
                        currentUrl = url

                        // Clear the directory once before downloading new images
                        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        dir?.let { clearDirectory(it) }

                        // Start a new download thread
                        isDownloading = true
                        Log.d("ImageDownloadService", "currentDownloadJob: $currentDownloadJob, isDownloading: $isDownloading")
                        currentDownloadJob = CoroutineScope(Dispatchers.IO).launch {
                            try {
                                // Download and save each image
                                imageUrls = fetchImageUrls(url)
                                val totalImages = imageUrls.size
                                var currentPosition = 0

                                Log.d("ImageDownloadService", "currentDownloadJob: $currentDownloadJob, isDownloading: $isDownloading")
                                for (imageUrl in imageUrls) {
                                    if (currentDownloadJob == null && !isDownloading) {
                                        Log.i("ImageDownloadService", "Download interrupted.")
                                        break // Exit loop if thread is interrupted
                                    }

                                    val savedPath = downloadAndSaveImage(imageUrl)
                                    if (savedPath != null) {
                                        currentPosition++
                                        broadcastImageDownloaded(savedPath, currentPosition, totalImages)
                                    }
                                }
                            } catch (e : InterruptedException) {
                                Log.i("ImageDownloadService", "Download interrupted.")
                            } finally {
                                Log.d("ImageDownloadService", "currentDownloadJob: $currentDownloadJob, isDownloading: $isDownloading")
                                isDownloading = false
                                currentDownloadJob = null
                                stopSelf()
                            }
                        }
                    }
                }
            }
            "stop" -> {
                Log.d("ImageDownloadService", "currentDownloadJob: $currentDownloadJob, isDownloading: $isDownloading")
                stopCurrentDownload()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun stopCurrentDownload() {
        Log.d("ImageDownloadService", "Stopping current download thread.")

        currentDownloadJob?.let {
            if (it.isActive) {
                Log.d("ImageDownloadService", "Cancelling current download job.")
                it.cancel()
            } else {
                Log.d("ImageDownloadService", "Job is not active, nothing to cancel.")
            }
        } ?: Log.d("ImageDownloadService", "No job to cancel.")

        isDownloading = false
        currentDownloadJob = null   // Ensure job is reset
        currentUrl = null // Clear the current URL
        Log.d("ImageDownloadService", "Download stopped and URL cleared.")
    }

    private fun broadcastImageDownloaded(imagePath : String, position: Int, total: Int) {
        val intent = Intent(BROADCAST_ACTION).apply {
            putExtra(EXTRA_IMAGE_PATH, imagePath)
            putExtra(EXTRA_PROGRESS, position)
            putExtra(EXTRA_TOTAL, total)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private suspend fun downloadAndSaveImage(imageURL : String) : String? {
        val url = URL(imageURL)
        val conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
        )
        try {
            val fileName = imageURL.substringAfterLast("/")
            val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: return null
            val file = File(dir, fileName)

            // Sleep to simulate network delay
            delay(500)

            conn.inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("ImageDownloadService", "Image saved at: ${file.absolutePath}")
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("ImageDownloadService", "Error downloading image: $imageURL, ${e.message}", e)
            return null
        } finally {
            conn.disconnect()
        }
    }

    private fun fetchImageUrls(url : String) : MutableList<String> {
        val imageUrls = mutableListOf<String>()
        try {
            // Fetch and parse the HTML
            val document = org.jsoup.Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .get()

            // Select all <img> tags and extract the `src` attribute
            val elements = document.select("img[src]")
            for (element in elements) {
                val imgUrl = element.absUrl("src")
                if (imgUrl.matches(Regex(".*\\.(jpg|jpeg|png|gif)$", RegexOption.IGNORE_CASE))) {
                    imageUrls.add(imgUrl)
                }
            }
            Log.d("ImageDownloadService", "Fetched image URLs: $imageUrls")
        } catch (e: Exception) {
            Log.e("ImageDownloadService", "Error fetching image URLs: ${e.message}", e)
        }
        return imageUrls.take(20).toMutableList()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}