package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.ServiceApplication
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class TTOnlineRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun testAuthenticatedUserCanSaveProfileAndCreateApplication() = runBlocking {
        val email = "customer_${System.currentTimeMillis()}@test.com"
        val uid = signInTestUser(email)
        val repository = TTOnlineRepository(firestore, auth)

        // 1. Save Profile
        val profile = UserProfile(
            id = uid,
            email = email,
            name = "Test Applicant",
            phone = "9876543210",
            role = "customer"
        )
        repository.saveUserProfile(profile)

        val fetchedProfile = repository.observeUserProfile(uid).first()
        assertNotNull("Profile should exist", fetchedProfile)
        assertEquals("Test Applicant", fetchedProfile?.name)

        // 2. Create Application
        val appId = "TT-2026-TEST-${System.currentTimeMillis() % 10000}"
        val app = ServiceApplication(
            applicationId = appId,
            userId = uid,
            customerName = "Test Applicant",
            customerEmail = email,
            customerPhone = "9876543210",
            serviceId = "pan_card",
            serviceName = "PAN Card Assistance",
            status = "Pending",
            paymentStatus = "Pending",
            consentGiven = true
        )
        val createdId = repository.createApplication(app)
        assertEquals(appId, createdId)

        // 3. Query Applications
        val apps = repository.observeUserApplications(uid).first()
        assertTrue("Applications list should contain created app", apps.any { it.applicationId == appId })
    }

    @Test
    fun testUnauthenticatedUserCannotCreateApplication() = runBlocking {
        auth.signOut()
        val repository = TTOnlineRepository(firestore, auth)
        val app = ServiceApplication(
            applicationId = "TT-FAIL-1",
            userId = "someone",
            customerName = "Unauth User",
            serviceId = "pan_card",
            serviceName = "PAN Card",
            status = "Pending",
            paymentStatus = "Pending",
            consentGiven = true
        )

        try {
            repository.createApplication(app)
            fail("Expected exception when unauthenticated")
        } catch (e: IllegalStateException) {
            assertTrue(e.message?.contains("authenticated") == true)
        }
    }
}
