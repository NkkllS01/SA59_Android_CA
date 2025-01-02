package iss.nus.edu.sg.fragments.workshop.ca5

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import iss.nus.edu.sg.fragments.workshop.ca5.databinding.ActivityDashboardBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class DashboardActivity : AppCompatActivity() {

    private val apiUrl = "http://10.0.2.2:5125/api/adimage"
    private lateinit var imageView : ImageView
    private lateinit var binding: ActivityDashboardBinding
    private val handler = Handler()
    private val fetchTask = object : Runnable {
        override fun run() {
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userType = sharedPreferences.getString("userType", "")
            if (userType != "paid") {
                fetchImage()
            }
            handler.postDelayed(this, 3000)
        }
    }
    // 定义定时任务，10 分钟后自动登出
    private val logoutRunnable = Runnable {
        Toast.makeText(this@DashboardActivity, "You have been logged out due to inactivity.", Toast.LENGTH_SHORT).show()
        logout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = findViewById<ImageView>(R.id.imageView)

        // 检查用户是否已登录
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userType = sharedPreferences.getString("userType","")
        if(userType == "paid"){
            imageView.visibility = View.GONE
        }else{
            handler.post(fetchTask)
        }

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            // 用户未登录，跳转到登录页面
            val intent = Intent(this@DashboardActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // 从 SharedPreferences 获取用户名
        val username = sharedPreferences.getString("username", "")

        // 显示用户名
        binding.welcomeTextView.text = "Welcome, $username!"

        // 设置 10 分钟的定时任务
        resetLogoutTimer()

        // 用户点击退出按钮时手动登出
        binding.logoutButton.setOnClickListener {
            logout()
        }
    }
    private fun fetchImage(){
        val client = OkHttpClient()
        val request = Request.Builder().url(apiUrl).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@DashboardActivity,"Failed to fetch image: ${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val jsonObject = JSONObject(responseBody)
                        val imageUrl = jsonObject.getString("imageUrl")

                        runOnUiThread {
                            Glide.with(this@DashboardActivity)
                                .load(imageUrl)
                                .into(imageView)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@DashboardActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    runOnUiThread {
                        Toast.makeText(this@DashboardActivity, "Failed to fetch image: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })


    }

    // 登出方法
    private fun logout() {

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()


        val intent = Intent(this@DashboardActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 重置定时任务
    private fun resetLogoutTimer() {
        // 取消之前的任务
        handler.removeCallbacks(logoutRunnable)

        // 重新开始定时任务
        handler.postDelayed(logoutRunnable, 600000) // 600000 毫秒 = 10 分钟
    }

    override fun onPause() {
        super.onPause()
        // 暂停时取消定时任务，防止界面被暂停时定时任务仍在执行
        handler.removeCallbacks(logoutRunnable)
    }

    override fun onResume() {
        super.onResume()
        // 如果用户回到界面，则重新开始定时任务
        resetLogoutTimer()
    }

    // 监听用户交互事件，如触摸屏幕或点击按钮
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 如果屏幕被触摸，则重置定时任务
        resetLogoutTimer()
        return super.onTouchEvent(event)
    }

    // 监听点击事件
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        // 如果用户点击任何地方，重置定时任务
        if (event != null) {
            resetLogoutTimer()
        }
        return super.dispatchTouchEvent(event)
    }
}
