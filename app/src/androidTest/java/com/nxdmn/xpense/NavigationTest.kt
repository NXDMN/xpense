package com.nxdmn.xpense

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.nxdmn.xpense.navigation.Route
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: TestNavHostController

    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            XpenseApp(navController = navController)
        }
    }

    // Unit test
    @Test
    fun verifyStartDestination() {
        composeTestRule
            .onNodeWithContentDescription("ExpenseList")
            .assertIsDisplayed()
    }

    @Test
    fun navigateToExpenseDetail() {
        composeTestRule.onNodeWithContentDescription("Add")
            .performClick()

        assertTrue(
            navController.currentBackStackEntry?.destination?.hasRoute<Route.ExpenseDetail>()
                ?: false
        )
    }

    @Test
    fun navigateToSettings() {
        composeTestRule.onNodeWithContentDescription(label = "Settings").performClick()
        assertTrue(
            navController.currentBackStackEntry?.destination?.hasRoute<Route.Settings>()
                ?: false
        )
    }

    @Test
    fun navigateToCategoryDetail() {
        composeTestRule.onNodeWithContentDescription(label = "Settings").performClick()
        composeTestRule.onNodeWithContentDescription(label = "Add Category").performClick()
        assertTrue(
            navController.currentBackStackEntry?.destination?.hasRoute<Route.CategoryDetail>()
                ?: false
        )
    }
}