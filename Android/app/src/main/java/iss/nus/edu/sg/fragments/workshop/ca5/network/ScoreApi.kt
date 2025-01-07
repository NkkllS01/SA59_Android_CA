package iss.nus.edu.sg.fragments.workshop.ca5.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ScoreApi {
    @POST("api/scores/add")
    fun addScore(@Body scoreDto: ScoreDto): Call<Void>
}