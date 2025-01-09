package iss.nus.edu.sg.fragments.workshop.ca5

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import iss.nus.edu.sg.fragments.workshop.ca5.databinding.ActivityLeaderboardBinding
import iss.nus.edu.sg.fragments.workshop.ca5.FetchActivity
import iss.nus.edu.sg.fragments.workshop.ca5.network.ApiClient
import iss.nus.edu.sg.fragments.workshop.ca5.network.LeaderboardApi
import iss.nus.edu.sg.fragments.workshop.ca5.network.LeaderboardRequest
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import retrofit2.Response

//Team03 Kuo Chi
class LeaderboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaderboardBinding
    private var scoreList: MutableList<Score> = mutableListOf()
    private val leaderboardApi = ApiClient.retrofit.create(LeaderboardApi::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(R.id.logout_fragment_container, LogoutFragment())
            .commit()

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userType = sharedPreferences.getString("userType","")
        if (userType == "free") {
            val adFragment = AdFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.adFragmentContainer, adFragment)
                .commit()
        }

        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButtons()

        lifecycleScope.launch {
            val scoreList = fetchRecord(5)
            val username = intent.getStringExtra("username")?: String
            val completionTime = intent.getStringExtra("completionTime")?: String
            if (scoreList.isNotEmpty()) {
                displayLeaderboard(scoreList, username.toString(), completionTime.toString())
            } else {
                Log.e("LeaderboardActivity", "No scores fetched")
                Toast.makeText(this@LeaderboardActivity, "No record data available", Toast
                    .LENGTH_SHORT).show()
            }
        }
    }

    private fun initButtons() {
        binding.apply {
            closeButton.setOnClickListener {
                val intent = Intent(this@LeaderboardActivity, FetchActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private suspend fun fetchRecord(top: Int): List<Score> {
        return try {
            val request = LeaderboardRequest(top)
            val response = leaderboardApi.getLeaderboard(request)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                Log.e("LeaderboardActivity", "Server error: ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("LeaderboardActivity", "Network error: ${e.message}")
            emptyList()
        }
    }

    protected fun displayLeaderboard(scoreList: List<Score>, username: String, completionTime: String) {
        binding.apply {
            val tableLayout = leaderboardTable
            val rowCount = tableLayout.childCount
            if (rowCount > 1) {
                tableLayout.removeViews(1, rowCount - 1)
            }

            for (score in scoreList) {
                val isCurrentPlaythrough = score.username == username && score.completionTime == completionTime
                val tableRow = TableRow(this@LeaderboardActivity)

                val rankTextView = TextView(this@LeaderboardActivity).apply {
                    text = score.rank
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(10, 50, 10, 50)
                    textSize = 18f
                    if (isCurrentPlaythrough) {
                        setBackgroundColor(ContextCompat.getColor(
                            this@LeaderboardActivity, R.color.light_yellow))
                        setTextColor(ContextCompat.getColor(
                                this@LeaderboardActivity, R.color.black))
                        setTypeface(null, Typeface.BOLD)
                    } else {
                        setTextColor(ContextCompat.getColor(
                                this@LeaderboardActivity, R.color.dark_grey))
                    }
                }

                val usernameTextView = TextView(this@LeaderboardActivity).apply {
                    text = score.username
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(10, 50, 10, 50)
                    textSize = 18f
                    if (isCurrentPlaythrough) {
                        setBackgroundColor(ContextCompat.getColor(
                            this@LeaderboardActivity, R.color.light_yellow))
                        setTextColor(ContextCompat.getColor(
                            this@LeaderboardActivity, R.color.black))
                        setTypeface(null, Typeface.BOLD)
                    } else {
                        setTextColor(ContextCompat.getColor(
                            this@LeaderboardActivity, R.color.dark_grey))
                    }
                }

                val timeTextView = TextView(this@LeaderboardActivity).apply {
                    text = score.completionTime
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(10, 50, 10, 50)
                    textSize = 18f
                    if (isCurrentPlaythrough) {
                        setBackgroundColor(ContextCompat.getColor(
                            this@LeaderboardActivity, R.color.light_yellow))
                        setTextColor(ContextCompat.getColor(
                            this@LeaderboardActivity, R.color.black))
                        setTypeface(null, Typeface.BOLD)
                    } else {
                        setTextColor(ContextCompat.getColor(
                            this@LeaderboardActivity, R.color.dark_grey))
                    }
                }

                tableRow.addView(rankTextView)
                tableRow.addView(usernameTextView)
                tableRow.addView(timeTextView)

                tableLayout.addView(tableRow)
            }
        }
    }

}

    /*
    protected fun fetchRecord(endPt: String, top: Int): MutableList<Score> {
        var conn: HttpURLConnection? = null

        return try {
            val url = URL(endPt)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true

            writeToServer(conn, top)

            if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                readFromServer(conn)
            } else {
                mutableListOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        } finally {
            conn?.disconnect()
        }
    }

    protected fun writeToServer(conn: HttpURLConnection, top:Int) {
        conn.setRequestProperty("Content-Type","application/json")

        val postData = JSONObject().apply {
            put("Top", top)
        }

        Log.d("LeaderboardActivity", "Request payload: $postData")

        val out = OutputStreamWriter(conn.outputStream)
        out.write(postData.toString())
        out.flush()
        out.close()
    }

    protected fun readFromServer(conn: HttpURLConnection): MutableList<Score> {
        val inputStream = conn.inputStream.bufferedReader()
        val response = inputStream.use { it.readText() }

        val jsonArray = JSONArray(response)
        val scores = mutableListOf<Score>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val rank = jsonObject.getString("rank")
            val username = jsonObject.getString("username")
            val completionTime = jsonObject.getString("completionTime")
            scores.add(Score(rank, username, completionTime))
        }

        return scores
    }
    */

