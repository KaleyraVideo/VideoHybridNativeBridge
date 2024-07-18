package com.kaleyra.video_sdk.ui.call.inputmessagehandle

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kaleyra.video.conference.Call
import com.kaleyra.video.conference.CallParticipants
import com.kaleyra.video.conference.Input
import com.kaleyra.video.conference.Inputs
import com.kaleyra.video_common_ui.CallUI
import com.kaleyra.video_common_ui.CollaborationViewModel
import com.kaleyra.video_common_ui.ConferenceUI
import com.kaleyra.video_common_ui.call.CameraStreamConstants
import com.kaleyra.video_sdk.R
import com.kaleyra.video_sdk.call.bottomsheetnew.inputmessage.view.InputMessageDuration
import com.kaleyra.video_sdk.call.callactions.viewmodel.CallActionsViewModel
import com.kaleyra.video_sdk.call.mapper.InputMapper.isMyCameraEnabled
import com.kaleyra.video_sdk.call.mapper.InputMapper.isMyMicEnabled
import com.kaleyra.video_sdk.call.screennew.InputMessageDragHandleTag
import com.kaleyra.video_sdk.call.screennew.InputMessageHandle
import com.kaleyra.video_utils.MutableSharedStateFlow
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class InputMessageHandleTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val callParticipants = mockk<CallParticipants>()
    private val call = mockk<CallUI>(relaxed = true)
    private val conference = mockk<ConferenceUI>(relaxed = true)
    private lateinit var callActionsViewModel: CallActionsViewModel

    private var myVideo: Input.Video.My = mockk(relaxed = true)
    private var myMic: Input.Audio = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkObject(com.kaleyra.video_sdk.call.mapper.InputMapper)
        with(callParticipants) {
            every { me } returns mockk(relaxed = true) {
                every { restrictions } returns mockk {
                    every { camera } returns MutableStateFlow(mockk() {
                        every { usage } returns false
                    })
                }
                every { streams } returns MutableStateFlow(listOf(mockk(relaxed = true) {
                    every { id } returns CameraStreamConstants.CAMERA_STREAM_ID
                    every { video } returns MutableStateFlow(myVideo)
                    every { audio } returns MutableStateFlow(myMic)
                }))
            }
            every { creator() } returns mockk(relaxed = true)
            every { others } returns listOf(mockk(relaxed = true))
        }
        with(call) {
            every { participants } returns MutableStateFlow(callParticipants)
            every { state } returns MutableStateFlow(Call.State.Connected)
            every { preferredType } returns MutableStateFlow(Call.PreferredType.audioVideo())
            every { inputs } returns mockk {
                coEvery { request(any(), Inputs.Type.Microphone) } returns Inputs.RequestResult.Success(myMic)
                every { availableInputs } returns MutableStateFlow(setOf(myVideo, myMic))
            }
        }
        with(conference) {
            every { call } returns MutableSharedStateFlow<CallUI>(this@InputMessageHandleTest.call)
        }
        callActionsViewModel = CallActionsViewModel {
           CollaborationViewModel.Configuration.Success(conference, mockk(), mockk(relaxed = true), MutableStateFlow(mockk()))
        }
        mockkObject(CallActionsViewModel.Companion)
        every { CallActionsViewModel.provideFactory(any()) } returns object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return callActionsViewModel as T
            }
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun nullInputMessage_dragHandleIsDisplayed() {
        composeTestRule.setContent {
            InputMessageHandle()
        }
        composeTestRule.onNodeWithTag(InputMessageDragHandleTag).assertIsDisplayed()
    }

    @Test
    fun testInputMessageHandleOnMicInputMessage() = runTest {
        every { myMic.enabled } returns MutableStateFlow(false)
        every { myMic.tryEnable() } returns true
        val microphone = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_microphone)
        val on = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_on)
        composeTestRule.setContent {
            InputMessageHandle()
        }
        advanceUntilIdle()
        composeTestRule.awaitIdle()

        callActionsViewModel.toggleMic(mockk<FragmentActivity>(relaxed = true))

        composeTestRule.onNodeWithText(microphone).assertIsDisplayed()
        composeTestRule.onNodeWithText(on).assertIsDisplayed()
        composeTestRule.onNodeWithTag(InputMessageDragHandleTag).assertDoesNotExist()
        composeTestRule.mainClock.advanceTimeBy(InputMessageDuration)
        composeTestRule.onNodeWithText(microphone).assertDoesNotExist()
        composeTestRule.onNodeWithText(on).assertDoesNotExist()
        composeTestRule.onNodeWithTag(InputMessageDragHandleTag).assertIsDisplayed()
    }

    @Test
    fun testInputMessageHandleOnCameraInputMessage() = runTest {
        every { myVideo.enabled } returns MutableStateFlow(false)
        every { myVideo.tryEnable() } returns true
        val camera = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_camera)
        val on = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_on)
        composeTestRule.setContent {
            InputMessageHandle()
        }
        advanceUntilIdle()
        composeTestRule.awaitIdle()

        callActionsViewModel.toggleCamera(mockk<FragmentActivity>(relaxed = true))
        composeTestRule.onNodeWithText(camera).assertIsDisplayed()
        composeTestRule.onNodeWithText(on).assertIsDisplayed()
        composeTestRule.onNodeWithTag(InputMessageDragHandleTag).assertDoesNotExist()
        composeTestRule.mainClock.advanceTimeBy(InputMessageDuration)
        composeTestRule.onNodeWithText(camera).assertDoesNotExist()
        composeTestRule.onNodeWithText(on).assertDoesNotExist()
        composeTestRule.onNodeWithTag(InputMessageDragHandleTag).assertIsDisplayed()
    }

    @Test
    fun testInputMessageHandleOffMicInputMessage() = runTest {
        every { myMic.enabled } returns MutableStateFlow(true)
        every { myMic.tryDisable() } returns true
        val microphone = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_microphone)
        val off = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_off)
        composeTestRule.setContent {
            InputMessageHandle()
        }
        advanceUntilIdle()
        composeTestRule.awaitIdle()

        callActionsViewModel.toggleMic(mockk<FragmentActivity>(relaxed = true))

        composeTestRule.onNodeWithText(microphone).assertIsDisplayed()
        composeTestRule.onNodeWithText(off).assertIsDisplayed()
        composeTestRule.onNodeWithTag(InputMessageDragHandleTag).assertDoesNotExist()
        composeTestRule.mainClock.advanceTimeBy(InputMessageDuration)
        composeTestRule.onNodeWithText(microphone).assertDoesNotExist()
        composeTestRule.onNodeWithText(off).assertDoesNotExist()
        composeTestRule.onNodeWithTag(InputMessageDragHandleTag).assertIsDisplayed()
    }

    @Test
    fun testInputMessageHandleOffCameraInputMessage() = runTest {
        every { myVideo.enabled } returns MutableStateFlow(true)
        every { myVideo.tryDisable() } returns true
        val camera = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_camera)
        val off = composeTestRule.activity.getString(R.string.kaleyra_call_sheet_off)
        composeTestRule.setContent {
            InputMessageHandle()
        }
        advanceUntilIdle()
        composeTestRule.awaitIdle()

        callActionsViewModel.toggleCamera(mockk<FragmentActivity>(relaxed = true))
        composeTestRule.onNodeWithText(camera).assertIsDisplayed()
        composeTestRule.onNodeWithText(off).assertIsDisplayed()
        composeTestRule.onNodeWithTag(InputMessageDragHandleTag).assertDoesNotExist()
        composeTestRule.mainClock.advanceTimeBy(InputMessageDuration)
        composeTestRule.onNodeWithText(camera).assertDoesNotExist()
        composeTestRule.onNodeWithText(off).assertDoesNotExist()
        composeTestRule.onNodeWithTag(InputMessageDragHandleTag).assertIsDisplayed()
    }
}
