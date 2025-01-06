package iss.nus.edu.sg.fragments.workshop.ca5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.fragments.workshop.ca5.ImageDownloadService.Companion.clearDirectory
import iss.nus.edu.sg.fragments.workshop.ca5.databinding.ActivityFetchBinding
import java.util.ArrayList

class FetchActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFetchBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter : ImageAdapter

    private var isDownloading = false
    private val placeholderImages = MutableList(20) { R.drawable.placeholder } // 20 placeholders
    private var currentImageIndex = 0

    private val imageDownloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val progress = intent.getIntExtra(ImageDownloadService.EXTRA_PROGRESS, 0)
            val total = intent.getIntExtra(ImageDownloadService.EXTRA_TOTAL, 20)
            val imagePath = intent.getStringExtra(ImageDownloadService.EXTRA_IMAGE_PATH)

            Log.d("FetchActivity", "Broadcast received - Progress: $progress, Total: $total, Path: $imagePath")

            if (imagePath != null && currentImageIndex < placeholderImages.size) {
                runOnUiThread {
                    imageAdapter.updateImageAtPosition(currentImageIndex, imagePath)
                    currentImageIndex++
                }
            }
            updateProgress(progress, total)
        }
    }

    private fun updateProgress(progress: Int, total: Int) {
        binding.apply {
            progressText.text = "Downloading $progress of $total images..."
            progressBar.progress = progress

            if (progress == total) {
                isDownloading = false
                progressBar.visibility = View.GONE
                progressText.text = "All images downloaded!\nPlease select 6 images!"
                nextButton.visibility = View.VISIBLE
                imageAdapter.enableSelection()
                selectedCountText.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userType = sharedPreferences.getString("userType","")
        if (userType == "free") {
            val adFragment = AdFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.adFragmentContainer, adFragment)
                .commit()
        }

        binding = ActivityFetchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initRecyclerView()
        initButtons()
        initReceiver()
    }

    private fun initRecyclerView() {
        recyclerView = binding.imageGrid
        recyclerView.layoutManager = GridLayoutManager(this, 4) // 4 columns
        imageAdapter = ImageAdapter(placeholderImages.toMutableList()) { selectedCount ->
            // Update selected count dynamically
            binding.selectedCountText.text = "Selected: $selectedCount / 6"
        }
        recyclerView.adapter = imageAdapter
    }

    private fun initButtons() {
        binding.apply {
            fetchButton.setOnClickListener {
                val url = urlInput.text.toString()

                if (url.isEmpty()) {
                    showToast("Please input the url...")
                } else {
                    showToast("Downloading images...")
                    resetState()
                    binding.apply {
                        progressBar.visibility = View.VISIBLE
                        progressText.visibility = View.VISIBLE
                        progressBar.progress = 0
                    }
                    if (!isDownloading) {
                        startDownload(url)
                    } else {
                        stopDownload()
                        startDownload(url)
                    }

                }
            }

            nextButton.setOnClickListener {
                val selectedImages = imageAdapter.getSelectedImages()
                if (selectedImages.size == 6) {
                    val intent = Intent(this@FetchActivity, PlayActivity::class.java).apply {
                        putStringArrayListExtra("selectedImages", ArrayList(selectedImages))
                    }
                    startActivity(intent)
                } else {
                    showToast("Please select exactly 6 images.")
                }
            }
        }
    }

    private fun startDownload(url : String) {
        // Start new download
        isDownloading = true
        val intent = Intent(this, ImageDownloadService::class.java).apply {
            action = "download"
            putExtra("url", url)
        }
        startService(intent)
    }

    private fun stopDownload() {
        isDownloading = false
        val intent = Intent(this, ImageDownloadService::class.java).apply {
            action = "stop"
        }
        startService(intent)
        Log.d("FetchActivity",
            "Stopping current download.")
    }

    private fun resetState() {
        // Reset image list and progress UI
        currentImageIndex = 0
        imageAdapter.updateImages(placeholderImages.toMutableList())
        updateProgress(0, placeholderImages.size)

        // Clear previously downloaded images
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        dir?.let { clearDirectory(it) }
        Log.d("FetchActivity", "State reset - Images cleared, UI reset.")
    }

    private fun initReceiver() {
        val filter = IntentFilter()
        filter.addAction(ImageDownloadService.BROADCAST_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(imageDownloadReceiver, filter)
    }

    private fun showToast(msg : String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(imageDownloadReceiver)
        super.onDestroy()
    }
}