package dto

data class UserResponse(
    var email: String,
    var password: String,
    var roles: List<String>
)
