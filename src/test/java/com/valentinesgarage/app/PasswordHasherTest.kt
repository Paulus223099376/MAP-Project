package com.valentinesgarage.app

import com.valentinesgarage.app.util.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Sanity checks for the password hashing utility — these guard the
 * single most security-sensitive surface of the app.
 */
class PasswordHasherTest {

    @Test
    fun `verify returns true for the original password`() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("Garage123", salt)
        assertTrue(PasswordHasher.verify("Garage123", salt, hash))
    }

    @Test
    fun `verify returns false for a wrong password`() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("Garage123", salt)
        assertFalse(PasswordHasher.verify("garage123", salt, hash))
        assertFalse(PasswordHasher.verify("Garage1234", salt, hash))
        assertFalse(PasswordHasher.verify("", salt, hash))
    }

    @Test
    fun `same password with different salts produces different hashes`() {
        val a = PasswordHasher.newSalt()
        val b = PasswordHasher.newSalt()
        assertNotEquals(a, b)
        val ha = PasswordHasher.hash("Mechanic1", a)
        val hb = PasswordHasher.hash("Mechanic1", b)
        assertNotEquals(ha, hb)
    }

    @Test
    fun `same password and salt is deterministic`() {
        val salt = PasswordHasher.newSalt()
        val first = PasswordHasher.hash("Mechanic1", salt)
        val second = PasswordHasher.hash("Mechanic1", salt)
        assertEquals(first, second)
    }

    @Test
    fun `hashes are reasonably long`() {
        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash("anything", salt)
        // SHA-256 hex output is 64 chars
        assertEquals(64, hash.length)
    }
}
