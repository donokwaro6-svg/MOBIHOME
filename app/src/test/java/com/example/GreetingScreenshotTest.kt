package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.example.data.repository.SampleData
import com.example.ui.components.PropertyCard
import com.example.ui.screens.OpeningSplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.Currency
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun property_card_renders_and_captures() {
        val sampleProp = com.example.model.Property(
            id = "test-prop-card-1",
            title = "Modern Studio Apartment",
            description = "Cozy and convenient",
            city = "Nairobi",
            country = "Kenya",
            address = "Kilimani",
            pricePerNight = 5000,
            imageResIds = listOf(R.drawable.img_hero_banner)
        )
        var wishlistToggled = false
        var cardClicked = false

        composeTestRule.setContent {
            MyApplicationTheme {
                PropertyCard(
                    property = sampleProp,
                    isWishlisted = false,
                    currency = Currency.KES,
                    onWishlistToggle = { wishlistToggled = true },
                    onClick = { cardClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("wishlist_button_${sampleProp.id}").performClick()
        assertTrue("Wishlist toggle callback should be invoked", wishlistToggled)

        composeTestRule.onNodeWithTag("property_card_${sampleProp.id}").performClick()
        assertTrue("Property card click callback should be invoked", cardClicked)

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/property_card.png")
    }

    @Test
    fun opening_splash_screen_renders_with_name_logo_and_slogan() {
        var finished = false
        composeTestRule.setContent {
            MyApplicationTheme {
                OpeningSplashScreen(
                    onAnimationFinished = { finished = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("opening_splash_screen").assertExists()
        composeTestRule.onNodeWithTag("splash_logo", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("splash_app_name", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("splash_slogan", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText("FindSpace", useUnmergedTree = true).assertExists()

        composeTestRule.onNodeWithTag("opening_splash_screen").performClick()
        assertTrue("Clicking splash should finish animation immediately", finished)
    }
}
