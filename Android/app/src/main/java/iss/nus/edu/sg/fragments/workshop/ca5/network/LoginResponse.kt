package iss.nus.edu.sg.fragments.workshop.ca5.network

data class LoginResponse(
    val success: Boolean,
    val userType: String,
    val username: String // 确保这里有 username 属性
)
