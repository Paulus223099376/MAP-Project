package com.valentinesgarage.app

import com.valentinesgarage.app.util.Validators
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {

    @Test
    fun `username accepts a normal handle`() {
        assertNull(Validators.usernameProblem("john.doe"))
        assertNull(Validators.usernameProblem("mary_22"))
    }

    @Test
    fun `username rejects empty short or invalid characters`() {
        assertNotNull(Validators.usernameProblem(""))
        assertNotNull(Validators.usernameProblem("ab"))
        assertNotNull(Validators.usernameProblem("Has Space"))
        assertNotNull(Validators.usernameProblem("emoji😀"))
    }

    @Test
    fun `password requires letters and digits and length`() {
        assertNotNull(Validators.passwordProblem(""))
        assertNotNull(Validators.passwordProblem("short"))
        assertNotNull(Validators.passwordProblem("alllettersnoNumber"))
        assertNotNull(Validators.passwordProblem("12345678"))
        assertNull(Validators.passwordProblem("Garage123"))
    }

    @Test
    fun `name requires letters and a reasonable length`() {
        assertNotNull(Validators.nameProblem(""))
        assertNotNull(Validators.nameProblem("J"))
        assertNotNull(Validators.nameProblem("12345"))
        assertNull(Validators.nameProblem("Valentine Garage"))
        assertNull(Validators.nameProblem("Anna-Marie O'Neil"))
    }

    @Test
    fun `email is optional but validated when provided`() {
        assertNull(Validators.emailProblem(""))
        assertNotNull(Validators.emailProblem("", required = true))
        assertNotNull(Validators.emailProblem("not-an-email"))
        assertNull(Validators.emailProblem("name@example.com"))
    }

    @Test
    fun `phone needs at least 7 digits when provided`() {
        assertNull(Validators.phoneProblem(""))
        assertNotNull(Validators.phoneProblem("12345"))
        assertNull(Validators.phoneProblem("+264 81 123 4567"))
    }
}
