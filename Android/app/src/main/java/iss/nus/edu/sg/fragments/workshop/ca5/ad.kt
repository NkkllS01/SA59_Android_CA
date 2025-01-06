package iss.nus.edu.sg.fragments.workshop.ca5

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import iss.nus.edu.sg.fragments.workshop.ca5.databinding.FragmentAdBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class AdFragment : Fragment() {

    private val apiUrl = "http://10.0.2.2:5125/api/adimage"
    private lateinit var binding: FragmentAdBinding
    private val handler = Handler()

    private val fetchTask = object : Runnable {
        override fun run() {
            fetchImage()
            handler.postDelayed(this, 30000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        handler.post(fetchTask)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(fetchTask)
    }

    private fun fetchImage() {
        val client = OkHttpClient()
        val request = Request.Builder().url(apiUrl).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Failed to fetch image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val imageUrl = jsonObject.getString("imageUrl")

                        activity?.runOnUiThread {
                            Glide.with(this@AdFragment)
                                .load(imageUrl)
                                .into(binding.adImageView)
                        }
                    } else {
                        activity?.runOnUiThread {
                            Toast.makeText(activity, "Response body is null", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(activity, "Failed to fetch image: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
