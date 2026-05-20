package com.valentinesgarage.app.util

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Salt + iterated SHA-256 password hashing. Pure JVM (no Android imports)
 * so it's straightforward to unit-test.
 *
 * NOTE: For a real production system you would normally reach for
 * Argon2 / BCrypt / scrypt. Iterated SHA-256 with a per-user random salt
 * is a reasonable academic-project compromise that *does not* store
 * passwords in plain text and *does* defeat rainbow tables.
 */
object PasswordHasher {

    private const val ITERATIONS = 12_000
    private const val SALT_BYTES = 16
    private const val ALGORITHM = "SHA-256"

    fun newSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        SecureRandom().nextBytes(bytes)
        return bytes.toHex()
    }

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance(ALGORITHM)
        var current: ByteArray = (salt + password).toByteArray(Charsets.UTF_8)
        repeat(ITERATIONS) {
            digest.reset()
            current = digest.digest(current)
        }
        return current.toHex()
    }

    fun verify(password: String, salt: String, expectedHash: String): Boolean {
        // Constant-time comparison to discourage timing attacks.
        val computed = hash(password, salt)
        if (computed.length != expectedHash.length) return false
        var diff = 0
        for (i in computed.indices) {
            diff = diff or (computed[i].code xor expectedHash[i].code)
        }
        return diff == 0
    }

    private fun ByteArray.toHex(): String =
        joinToString(separator = "") { byte -> "%02x".format(byte) }
}
