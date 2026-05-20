package com.valentinesgarage.app.util

object Validators {

    private val USERNAME_REGEX = Regex("^[a-z0-9_.]{3,20}$")
    private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private val NAME_REGEX = Regex("^[\\p{L} '\\-]{2,40}$")

    fun usernameProblem(value: String): String? = when {
        value.isBlank() -> "Username is required"
        value.length < 3 -> "Username must be at least 3 characters"
        !USERNAME_REGEX.matches(value.lowercase()) ->
            "Use 3–20 lowercase letters, digits, dot or underscore"
        else -> null
    }

    fun nameProblem(value: String): String? = when {
        value.isBlank() -> "Full name is required"
        !NAME_REGEX.matches(value.trim()) -> "Enter a valid name (letters, spaces, hyphens)"
        else -> null
    }

    fun passwordProblem(value: String): String? = when {
        value.isBlank() -> "Password is required"
        value.length < 6 -> "Password must be at least 6 characters"
        value.none { it.isDigit() } -> "Add at least one number"
        value.none { it.isLetter() } -> "Add at least one letter"
        else -> null
    }

    fun emailProblem(value: String, required: Boolean = false): String? {
        if (value.isBlank()) return if (required) "Email is required" else null
        return if (!EMAIL_REGEX.matches(value.trim())) "Enter a valid email" else null
    }

    fun phoneProblem(value: String, required: Boolean = false): String? {
        if (value.isBlank()) return if (required) "Phone is required" else null
        val digits = value.filter { it.isDigit() }
        return if (digits.length < 7) "Enter a valid phone number" else null
    }
}
