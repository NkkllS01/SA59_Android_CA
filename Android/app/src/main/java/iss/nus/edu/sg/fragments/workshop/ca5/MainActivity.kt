package iss.nus.edu.sg.fragments.workshop.ca5

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.fragments.workshop.ca5.databinding.ActivityMainBinding
import iss.nus.edu.sg.fragments.workshop.ca5.network.ApiClient
import iss.nus.edu.sg.fragments.workshop.ca5.network.AuthApi
import iss.nus.edu.sg.fragments.workshop.ca5.network.LoginRequest
import iss.nus.edu.sg.fragments.workshop.ca5.network.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import okhttp3.*
import java.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import com.bumptech.glide.Glide
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val apiUrl = "http://10.0.2.2:5125/api/adimage"
    private lateinit var imageView :ImageView
    private val handler = Handler(Looper.getMainLooper())
    private val fetchTask = object : Runnable {
        override fun run() {
            fetchImage()
            handler.postDelayed(this, 3000)
        }
    }
    private lateinit var binding: ActivityMainBinding
    private val authApi = ApiClient.retrofit.create(AuthApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = findViewById<ImageView>(R.id.imageView)
        handler.post(fetchTask)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val request = LoginRequest(username, password)
            authApi.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.success == true) {
                            Toast.makeText(this@MainActivity, "Welcome, ${loginResponse.userType}!", Toast.LENGTH_LONG).show()

                            // 保存登录状态
                            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isLoggedIn", true)  // 设置登录状态为 true
                            editor.putString("username", username)  // 保存用户名
                            editor.apply()

                            // 跳转到 DashboardActivity
                            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                            intent.putExtra("username", username)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Invalid credentials.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Server error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    private fun fetchImage(){
        val client =OkHttpClient()
        val request = Request.Builder().url(apiUrl).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity,"Failed to fetch image: ${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val imageUrl = jsonObject.getString("imageUrl")

                        runOnUiThread {
                            Glide.with(this@MainActivity)
                                .load(imageUrl)
                                .into(imageView)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Failed to fetch image: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })


    }
}
