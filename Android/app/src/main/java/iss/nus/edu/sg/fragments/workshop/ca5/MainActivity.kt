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


    private val handler = Handler(Looper.getMainLooper())

    private lateinit var binding: ActivityMainBinding
    private val authApi = ApiClient.retrofit.create(AuthApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val request = LoginRequest(username, password)
            authApi.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.success == true) {
                            Toast.makeText(this@MainActivity, "Welcome, " + username + "!", Toast.LENGTH_LONG).show()

                            // 保存登录状态
                            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putBoolean("isLoggedIn", true)  // 设置登录状态为 true
                            editor.putString("username", username)  // 保存用户名
                            editor.putString("userType",loginResponse.userType)
                            editor.apply()

                            // 跳转到 DashboardActivity
                            val intent = Intent(this@MainActivity, FetchActivity::class.java)
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

}
