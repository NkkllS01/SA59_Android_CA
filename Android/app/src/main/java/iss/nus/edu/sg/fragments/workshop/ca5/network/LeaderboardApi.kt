package iss.nus.edu.sg.fragments.workshop.ca5.network

import iss.nus.edu.sg.fragments.workshop.ca5.Score
import org.jsoup.helper.HttpConnection
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface LeaderboardApi {
    @POST("api/leaderboard/display")
    suspend fun getLeaderboard(@Body requestBody: LeaderboardRequest): Response<List<Score>>
}