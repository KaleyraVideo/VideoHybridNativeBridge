package com.kaleyra.video_sdk.call.bottomsheetnew.sheetcontent

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEqualTo
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.kaleyra.video_sdk.R
import com.kaleyra.video_sdk.common.immutablecollections.ImmutableList
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class VerticalSheetContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testShowMoreItemTrue() {
        val moreDescription = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_more_actions)
        composeTestRule.setContent {
            VerticalSheetContent(
                actions = ImmutableList(),
                showMoreItem = true,
                onMoreItemClick = { },
                onItemsPlaced = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(moreDescription).assertIsDisplayed()
    }

    @Test
    fun testShowMoreItemFalse() {
        val moreDescription = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_more_actions)
        composeTestRule.setContent {
            VerticalSheetContent(
                actions = ImmutableList(),
                showMoreItem = false,
                onMoreItemClick = { },
                onItemsPlaced = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(moreDescription).assertDoesNotExist()
    }

    @Test
    fun testOnMoreItemClick() {
        var isMoreClicked = false
        val moreDescription = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_more_actions)
        composeTestRule.setContent {
            VerticalSheetContent(
                actions = ImmutableList(),
                showMoreItem = true,
                onMoreItemClick = { isMoreClicked = true },
                onItemsPlaced = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(moreDescription).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(moreDescription).performClick()
        assertEquals(true, isMoreClicked)
    }

    @Test
    fun testSheetContentItemsPlacing() {
        val moreDescription = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_more_actions)
        composeTestRule.setContent {
            VerticalSheetContent(
                actions = ImmutableList(
                    listOf(
                        { _, _ -> Text("text1") },
                        { _, _ -> Text("text2") },
                        { _, _ -> Text("text3") }
                    )
                ),
                showMoreItem = true,
                onMoreItemClick = { },
                onItemsPlaced = { }
            )
        }
        val rootBounds = composeTestRule.onRoot().getBoundsInRoot()
        val childBounds1 = composeTestRule.onNodeWithText("text1").getBoundsInRoot()
        val childBounds2 = composeTestRule.onNodeWithText("text2").getBoundsInRoot()
        val childBounds3 = composeTestRule.onNodeWithText("text3").getBoundsInRoot()
        val moreChild = composeTestRule.onNodeWithContentDescription(moreDescription).getBoundsInRoot()
        childBounds1.bottom.assertIsEqualTo(rootBounds.bottom, "child 1 bottom")
        childBounds2.bottom.assertIsEqualTo(childBounds1.top - SheetContentItemSpacing, "child 2 bottom")
        childBounds3.bottom.assertIsEqualTo(childBounds2.top - SheetContentItemSpacing, "child 3 bottom")
        moreChild.bottom.assertIsEqualTo(childBounds3.top - SheetContentItemSpacing, "more child top")
        moreChild.top.assertIsEqualTo(rootBounds.top, "more child top")
    }

    @Test
    fun testOnItemsPlacedCallback() {
        var itemsCount = -1
        composeTestRule.setContent {
            VerticalSheetContent(
                modifier = Modifier.height(75.dp),
                actions = ImmutableList(
                    listOf(
                        { _, _ -> Spacer(Modifier.height(24.dp)) },
                        { _, _ -> Spacer(Modifier.height(24.dp)) },
                        { _, _ -> Spacer(Modifier.height(24.dp)) }
                    )
                ),
                showMoreItem = false,
                onMoreItemClick = { },
                onItemsPlaced = { itemsCount = it }
            )
        }
        assertEquals(2, itemsCount)
    }
}