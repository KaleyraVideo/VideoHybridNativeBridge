package com.kaleyra.video_sdk.ui.call.bottomsheetnew.sheetdragcontent

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEqualTo
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.kaleyra.video_sdk.R
import com.kaleyra.video_sdk.call.bottomsheetnew.sheetcontent.sheetitemslayout.SheetItemsSpacing
import com.kaleyra.video_sdk.call.bottomsheetnew.sheetdragcontent.VSheetDragContent
import com.kaleyra.video_sdk.call.bottomsheetnew.sheetdragcontent.VSheetDragHorizontalPadding
import com.kaleyra.video_sdk.call.bottomsheetnew.sheetdragcontent.VSheetDragVerticalPadding
import com.kaleyra.video_sdk.call.callactionnew.CallActionDefaults
import com.kaleyra.video_sdk.call.callactions.model.CallActionsUiState
import com.kaleyra.video_sdk.call.callactions.viewmodel.CallActionsViewModel
import com.kaleyra.video_sdk.call.screennew.AudioAction
import com.kaleyra.video_sdk.call.screennew.CameraAction
import com.kaleyra.video_sdk.call.screennew.ChatAction
import com.kaleyra.video_sdk.call.screennew.FileShareAction
import com.kaleyra.video_sdk.call.screennew.FlipCameraAction
import com.kaleyra.video_sdk.call.screennew.HangUpAction
import com.kaleyra.video_sdk.call.screennew.MicAction
import com.kaleyra.video_sdk.call.screennew.ModalSheetComponent
import com.kaleyra.video_sdk.call.screennew.ScreenShareAction
import com.kaleyra.video_sdk.call.screennew.VirtualBackgroundAction
import com.kaleyra.video_sdk.call.screennew.WhiteboardAction
import com.kaleyra.video_sdk.common.immutablecollections.ImmutableList
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VSheetDragContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val callActionsUiState = MutableStateFlow(CallActionsUiState())

    private val callActionsViewModel = mockk<CallActionsViewModel>(relaxed = true) {
        every { uiState } returns callActionsUiState
    }

    @Test
    fun userClicksHangUp_hangUpInvoked() {
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(HangUpAction())),
                onModalSheetComponentRequest = {}
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_hang_up)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        verify(exactly = 1) { callActionsViewModel.hangUp() }
    }

    @Test
    fun userTogglesMic_toggleMicInvoked() {
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(MicAction())),
                onModalSheetComponentRequest = {}
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_description_disable_microphone)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        verify(exactly = 1) { callActionsViewModel.toggleMic(any()) }
    }

    @Test
    fun userTogglesCamera_toggleCameraInvoked() {
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(CameraAction())),
                onModalSheetComponentRequest = {}
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_description_disable_camera)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        verify(exactly = 1) { callActionsViewModel.toggleCamera(any()) }
    }

    @Test
    fun userClicksChat_showChatInvoked() {
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(ChatAction())),
                onModalSheetComponentRequest = {}
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_chat)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        verify(exactly = 1) { callActionsViewModel.showChat(any()) }
    }

    @Test
    fun userClicksFlipCamera_switchCameraInvoked() {
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(FlipCameraAction())),
                onModalSheetComponentRequest = {}
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_flip_camera)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        verify(exactly = 1) { callActionsViewModel.switchCamera() }
    }

    @Test
    fun userClicksAudio_onModalSheetComponentRequestAudio() {
        var component: ModalSheetComponent? = null
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(AudioAction())),
                onModalSheetComponentRequest = { component = it }
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_audio)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        assertEquals(ModalSheetComponent.Audio, component)
    }

    @Test
    fun userClicksFileShare_onModalSheetComponentRequestFileShare() {
        var component: ModalSheetComponent? = null
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(FileShareAction())),
                onModalSheetComponentRequest = { component = it }
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_file_share)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        assertEquals(ModalSheetComponent.FileShare, component)
    }

    @Test
    fun userClicksWhiteboard_onModalSheetComponentRequestWhiteboard() {
        var component: ModalSheetComponent? = null
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(WhiteboardAction())),
                onModalSheetComponentRequest = { component = it }
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_whiteboard)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        assertEquals(ModalSheetComponent.Whiteboard, component)
    }

    @Test
    fun userClicksVirtualBackground_onModalSheetComponentRequestVirtualBackground() {
        var component: ModalSheetComponent? = null
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(VirtualBackgroundAction())),
                onModalSheetComponentRequest = { component = it }
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_virtual_background)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        assertEquals(ModalSheetComponent.VirtualBackground, component)
    }

    @Test
    fun userClicksScreenShareWhenEnabled_tryStopScreenShareInvoked() {
        every { callActionsViewModel.tryStopScreenShare() } returns true
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(ScreenShareAction())),
                onModalSheetComponentRequest = {}
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_screen_share)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        verify(exactly = 1) { callActionsViewModel.tryStopScreenShare() }
    }

    @Test
    fun userClicksScreenShareWhenNotEnabled_onModalSheetComponentRequestScreenShare() {
        every { callActionsViewModel.tryStopScreenShare() } returns false
        var component: ModalSheetComponent? = null
        composeTestRule.setContent {
            VSheetDragContent(
                viewModel = callActionsViewModel,
                callActions = ImmutableList(listOf(ScreenShareAction())),
                onModalSheetComponentRequest = { component = it }
            )
        }

        val text = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_screen_share)
        composeTestRule
            .onNodeWithContentDescription(text, useUnmergedTree = true)
            .assertIsDisplayed()
            .performClick()

        assertEquals(ModalSheetComponent.ScreenShare, component)
    }

    @Test
    fun testItemsPlacement() {
        val itemsPerColumn = 2
        val height = CallActionDefaults.MinButtonSize * itemsPerColumn + SheetItemsSpacing
        val flip = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_flip_camera)
        val camera = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_description_disable_camera)
        val mic = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_description_disable_microphone)
        val chat = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_chat)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(
                    listOf(
                        FlipCameraAction(),
                        CameraAction(),
                        MicAction(),
                        ChatAction()
                    )
                ),
                itemsPerColumn = itemsPerColumn,
                onHangUpClick = { },
                onMicToggle = { },
                onCameraToggle = { },
                onScreenShareToggle = { },
                onFlipCameraClick = { },
                onAudioClick = { },
                onChatClick = { },
                onFileShareClick = { },
                onWhiteboardClick = { },
                onVirtualBackgroundClick = { },
                modifier = Modifier.height(height)
            )
        }
        val rootBounds = composeTestRule.onRoot().getBoundsInRoot()
        val childBounds1 = composeTestRule.onNodeWithContentDescription(flip).getBoundsInRoot()
        val childBounds2 = composeTestRule.onNodeWithContentDescription(camera).getBoundsInRoot()
        val childBounds3 = composeTestRule.onNodeWithContentDescription(mic).getBoundsInRoot()
        val childBounds4 = composeTestRule.onNodeWithContentDescription(chat).getBoundsInRoot()
        childBounds1.top.assertIsEqualTo(childBounds2.bottom + VSheetDragVerticalPadding, "child 1 top bound")
        childBounds1.bottom.assertIsEqualTo(rootBounds.bottom, "child 1 bottom bound")
        childBounds2.top.assertIsEqualTo(rootBounds.top, "child 2 top bound")
        childBounds3.top.assertIsEqualTo(childBounds4.bottom + VSheetDragVerticalPadding, "child 3 top bound")
        childBounds3.bottom.assertIsEqualTo(rootBounds.bottom, "child 3 bottom bound")
        childBounds4.top.assertIsEqualTo(rootBounds.top, "child 4 top bound")
        childBounds3.left.assertIsEqualTo(childBounds1.right + VSheetDragHorizontalPadding, "child 3 left bound")
        childBounds4.left.assertIsEqualTo(childBounds2.right + VSheetDragHorizontalPadding, "child 4 left bound")
    }

    @Test
    fun testOnMicClick() {
        var isClicked: Boolean? = null
        val description = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_description_disable_microphone)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(listOf(MicAction())),
                onHangUpClick = { },
                onMicToggle = { isClicked = it },
                onCameraToggle = { },
                onScreenShareToggle = { },
                onFlipCameraClick = { },
                onAudioClick = { },
                onChatClick = { },
                onFileShareClick = { },
                onWhiteboardClick = { },
                onVirtualBackgroundClick = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(description).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(description).performClick()
        Assert.assertEquals(true, isClicked)
    }

    @Test
    fun testOnCameraClick() {
        var isClicked: Boolean? = null
        val description = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_description_disable_camera)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(listOf(CameraAction())),
                onHangUpClick = { },
                onMicToggle = { },
                onCameraToggle = { isClicked = it },
                onScreenShareToggle = { },
                onFlipCameraClick = { },
                onAudioClick = { },
                onChatClick = { },
                onFileShareClick = { },
                onWhiteboardClick = { },
                onVirtualBackgroundClick = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(description).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(description).performClick()
        Assert.assertEquals(true, isClicked)
    }

    @Test
    fun testOnScreenShareClick() {
        var isClicked: Boolean? = null
        val description = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_screen_share)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(listOf(ScreenShareAction())),
                onHangUpClick = { },
                onMicToggle = { },
                onCameraToggle = { },
                onScreenShareToggle = { isClicked = true },
                onFlipCameraClick = { },
                onAudioClick = { },
                onChatClick = { },
                onFileShareClick = { },
                onWhiteboardClick = { },
                onVirtualBackgroundClick = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(description).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(description).performClick()
        Assert.assertEquals(true, isClicked)
    }

    @Test
    fun testOnFlipCameraClick() {
        var isClicked = false
        val description = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_flip_camera)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(listOf(FlipCameraAction())),
                onHangUpClick = { },
                onMicToggle = { },
                onCameraToggle = { },
                onScreenShareToggle = { },
                onFlipCameraClick = { isClicked = true },
                onAudioClick = { },
                onChatClick = { },
                onFileShareClick = { },
                onWhiteboardClick = { },
                onVirtualBackgroundClick = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(description).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(description).performClick()
        Assert.assertEquals(true, isClicked)
    }

    @Test
    fun testOnAudioClick() {
        var isClicked = false
        val description = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_audio)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(listOf(AudioAction())),
                onHangUpClick = { },
                onMicToggle = { },
                onCameraToggle = { },
                onScreenShareToggle = { },
                onFlipCameraClick = { },
                onAudioClick = { isClicked = true },
                onChatClick = { },
                onFileShareClick = { },
                onWhiteboardClick = { },
                onVirtualBackgroundClick = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(description).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(description).performClick()
        Assert.assertEquals(true, isClicked)
    }

    @Test
    fun testOnChatClick() {
        var isClicked = false
        val description = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_chat)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(listOf(ChatAction())),
                onHangUpClick = { },
                onMicToggle = { },
                onCameraToggle = { },
                onScreenShareToggle = { },
                onFlipCameraClick = { },
                onAudioClick = { },
                onChatClick = { isClicked = true },
                onFileShareClick = { },
                onWhiteboardClick = { },
                onVirtualBackgroundClick = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(description).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(description).performClick()
        Assert.assertEquals(true, isClicked)
    }

    @Test
    fun testOnFileShareClick() {
        var isClicked = false
        val description = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_file_share)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(listOf(FileShareAction())),
                onHangUpClick = { },
                onMicToggle = { },
                onCameraToggle = { },
                onScreenShareToggle = { },
                onFlipCameraClick = { },
                onAudioClick = { },
                onChatClick = { },
                onFileShareClick = { isClicked = true },
                onWhiteboardClick = { },
                onVirtualBackgroundClick = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(description).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(description).performClick()
        Assert.assertEquals(true, isClicked)
    }

    @Test
    fun testOnWhiteboardClick() {
        var isClicked = false
        val description = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_whiteboard)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(listOf(WhiteboardAction())),
                onHangUpClick = { },
                onMicToggle = { },
                onCameraToggle = { },
                onScreenShareToggle = { },
                onFlipCameraClick = { },
                onAudioClick = { },
                onChatClick = { },
                onFileShareClick = { },
                onWhiteboardClick = { isClicked = true },
                onVirtualBackgroundClick = { }
            )
        }
        composeTestRule.onNodeWithContentDescription(description).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(description).performClick()
        Assert.assertEquals(true, isClicked)
    }

    @Test
    fun testOnVirtualBackgroundClick() {
        var isClicked = false
        val description = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_virtual_background)
        composeTestRule.setContent {
            VSheetDragContent(
                callActions = ImmutableList(listOf(VirtualBackgroundAction())),
                onHangUpClick = { },
                onMicToggle = { },
                onCameraToggle = { },
                onScreenShareToggle = { },
                onFlipCameraClick = { },
                onAudioClick = { },
                onChatClick = { },
                onFileShareClick = { },
                onWhiteboardClick = { },
                onVirtualBackgroundClick = { isClicked = true }
            )
        }
        composeTestRule.onNodeWithContentDescription(description).assertHasClickAction()
        composeTestRule.onNodeWithContentDescription(description).performClick()
        Assert.assertEquals(true, isClicked)
    }
}