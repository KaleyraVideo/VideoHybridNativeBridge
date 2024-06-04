package com.kaleyra.video_sdk.call.callactionnew

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kaleyra.video_sdk.R
import com.kaleyra.video_sdk.call.bottomsheetnew.sheetcontent.sheetitemslayout.SheetItemsSpacing
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class AnswerActionTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testDefaultButton() {
        val testTag = "testTag"
        composeTestRule.setContent {
            AnswerAction(
                extended = false,
                onClick = { },
                modifier = Modifier.testTag(testTag)
            )
        }
        val width =  CallActionDefaults.MinButtonSize * 2 + SheetItemsSpacing
        composeTestRule.onNodeWithTag(testTag).assertWidthIsEqualTo(width)
    }

    @Test
    fun testExtendedButton() {
        val testTag = "testTag"
        composeTestRule.setContent {
            AnswerAction(
                extended = true,
                onClick = { },
                modifier = Modifier.testTag(testTag)
            )
        }
        val width =  CallActionDefaults.MinButtonSize * 3 + SheetItemsSpacing * 2
        composeTestRule.onNodeWithTag(testTag).assertWidthIsEqualTo(width)
    }

    @Test
    fun testOnClickInvoked() {
        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_answer)
        var clicked = false
        composeTestRule.setContent {
            AnswerAction(onClick = { clicked = true }, extended = true)
        }
        composeTestRule.onNodeWithText(text).assertIsEnabled()
        composeTestRule.onNodeWithText(text).performClick()
        Assert.assertEquals(true, clicked)
    }
}