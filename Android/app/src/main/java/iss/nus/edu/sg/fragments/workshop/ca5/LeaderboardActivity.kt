package com.example.android_ca

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

//Team03 Kuo Chi
class LeaderboardActivity : AppCompatActivity() {}
   /*  private lateinit var binding: ActivityLeaderboardBinding
    private var scoreList: MutableList<Score> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButtons()

        Thread{
            val endPt = "http://10.0.2.2:5241/Leaderboard/Display"
            val top = 5
            scoreList = fetchRecord(endPt,top)

            if (scoreList.isNotEmpty()) {
                runOnUiThread {
                    displayLeaderboard(scoreList)
                }
            } else {
                Log.e("LeaderboardActivity", "No scores fetched")
            }
        }.start()
    }

    private fun initButtons() {
        binding.apply {
            closeButton.setOnClickListener {
                val intent = Intent(this@LeaderboardActivity, FetchActivity::class.java)
                startActivity(intent)
            }
        }
    }

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

    protected fun displayLeaderboard(scoreList: List<Score>) {
        binding.apply {
            val tableLayout = leaderboardTable
            val rowCount = tableLayout.childCount
            if (rowCount > 1) {
                tableLayout.removeViews(1, rowCount - 1)
            }

            for (score in scoreList) {
                val tableRow = TableRow(this@LeaderboardActivity)

                val rankTextView = TextView(this@LeaderboardActivity).apply {
                    text = score.rank
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(10, 50, 10, 50)
                    setTextColor(ContextCompat.getColor(this@LeaderboardActivity, R.color.dark_grey))
                    textSize = 18f
                }

                val usernameTextView = TextView(this@LeaderboardActivity).apply {
                    text = score.username
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(10, 50, 10, 50)
                    setTextColor(ContextCompat.getColor(this@LeaderboardActivity, R.color.dark_grey))
                    textSize = 18f
                }

                val timeTextView = TextView(this@LeaderboardActivity).apply {
                    text = score.completionTime
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 6f)
                    gravity = Gravity.CENTER_HORIZONTAL
                    setPadding(10, 50, 10, 50)
                    setTextColor(ContextCompat.getColor(this@LeaderboardActivity, R.color.dark_grey))
                    textSize = 18f
                }

                tableRow.addView(rankTextView)
                tableRow.addView(usernameTextView)
                tableRow.addView(timeTextView)

                tableLayout.addView(tableRow)
            }
        }
    }

} */