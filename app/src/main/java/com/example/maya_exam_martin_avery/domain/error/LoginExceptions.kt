package com.example.maya_exam_martin_avery.domain.error

/**
 * Expected login failure: the provided credentials do not match any stored user.
 *
 * Note: With the current DAO query (username + password), this represents either
 * "unknown username" or "wrong password" without distinguishing between them.
 */
class InvalidCredentialsException : Exception("Invalid credentials")

/**
 * Unexpected login failure: used to wrap data-layer/runtime issues (e.g., DB crashes).
 */
class LoginAppException(cause: Throwable) : Exception("Login failed", cause)

