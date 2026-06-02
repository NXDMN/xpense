package com.nxdmn.xpense

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.nxdmn.xpense.navigation.CategoryDetail
import com.nxdmn.xpense.navigation.ExpenseDetail
import com.nxdmn.xpense.navigation.ExpenseList
import com.nxdmn.xpense.navigation.NavigationState
import com.nxdmn.xpense.navigation.Navigator
import com.nxdmn.xpense.navigation.Settings
import com.nxdmn.xpense.navigation.rememberNavigationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navigationState: NavigationState
    lateinit var navigator: Navigator

    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            navigationState = rememberNavigationState(
                startRoute = ExpenseList,
                topLevelRoutes = setOf(ExpenseList, Settings),
            )
            navigator = remember { Navigator(navigationState) }

            XpenseApp(
                navigationState = navigationState,
                navigator = navigator
            )
        }
    }

    // Unit test
    @Test
    fun verifyStartDestination() {
        composeTestRule
            .onNodeWithContentDescription("ExpenseList")
            .assertIsDisplayed()

        // Assert the structural Nav 3 state matches expectations
        assertEquals(ExpenseList, navigationState.topLevelRoute)
        val currentStack = navigationState.backStacks[navigationState.topLevelRoute]
        assertEquals(ExpenseList, currentStack?.last())
    }

    @Test
    fun navigateToExpenseDetail() {
        composeTestRule.onNodeWithContentDescription("Add")
            .performClick()

        val currentStack = navigationState.backStacks[navigationState.topLevelRoute]

        assertTrue(currentStack?.last() is ExpenseDetail)
    }

    @Test
    fun navigateToSettings() {
        composeTestRule.onNodeWithContentDescription(label = "Settings").performClick()
        assertEquals(Settings, navigationState.topLevelRoute)

        val currentStack = navigationState.backStacks[navigationState.topLevelRoute]
        assertEquals(Settings, currentStack?.last())
    }

    @Test
    fun navigateToCategoryDetail() {
        composeTestRule.onNodeWithContentDescription(label = "Settings").performClick()
        composeTestRule.onNodeWithContentDescription(label = "Add Category").performClick()

        val currentStack = navigationState.backStacks[navigationState.topLevelRoute]

        assertTrue(currentStack?.last() is CategoryDetail)
    }
}