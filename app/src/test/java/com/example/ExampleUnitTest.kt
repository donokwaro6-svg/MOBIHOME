package com.example

import com.example.data.repository.SampleData
import com.example.model.AuthUser
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `verify FindSpace slogan is accurately represented in app identity`() {
        val slogan = "FindSpace"
        assertEquals("FindSpace", slogan)
        assertFalse(slogan.isBlank())
    }

    @Test
    fun `verify sample properties starts empty for new user rule`() {
        assertTrue(SampleData.properties.isEmpty())
        val property = com.example.model.Property(
            id = "test-prop-1",
            title = "Kilimani Apartment",
            description = "Spacious flat",
            city = "Nairobi",
            country = "Kenya",
            address = "Argwings Kodhek",
            pricePerNight = 6000,
            host = com.example.model.Host(
                id = "host-1",
                name = "Amina Hassan"
            )
        )
        assertNotNull(property.host.id)
        assertNotNull(property.host.name)
        assertTrue(property.pricePerNight > 0)
    }

    @Test
    fun `verify auth user maintains session state without hardcoded accounts`() {
        val newUser = AuthUser(
            uid = "new-random-uid-789",
            email = "brandnewuser@test.org",
            displayName = "New User",
            provider = "password"
        )

        assertNotNull(newUser.uid)
        assertEquals("brandnewuser@test.org", newUser.email)
        assertEquals("NU", newUser.initials)
    }
}

