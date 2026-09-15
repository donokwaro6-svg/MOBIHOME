package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.example.data.repository.SampleData
import com.example.ui.components.PropertyCard
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
        val sampleProp = SampleData.properties.first()
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
}
