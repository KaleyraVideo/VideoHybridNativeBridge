package com.kaleyra.video_sdk.call.callactionnew

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kaleyra.video_sdk.R
import com.kaleyra.video_sdk.call.audiooutput.model.AudioDeviceUi
import com.kaleyra.video_sdk.theme.KaleyraM3Theme

@Composable
internal fun AudioAction(
    audioDevice: AudioDeviceUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: Boolean = false,
) {
    val icon = painterResource(
        id = when (audioDevice) {
            AudioDeviceUi.LoudSpeaker -> R.drawable.ic_kaleyra_call_sheet_speaker
            AudioDeviceUi.WiredHeadset -> R.drawable.ic_kaleyra_call_sheet_wired_headset
            AudioDeviceUi.EarPiece -> R.drawable.ic_kaleyra_call_sheet_earpiece
            AudioDeviceUi.Muted -> R.drawable.ic_kaleyra_call_sheet_muted
            is AudioDeviceUi.Bluetooth -> R.drawable.ic_kaleyra_call_sheet_bluetooth
        }
    )
    val text = stringResource(id = R.string.kaleyra_call_sheet_audio)
    CallAction(
        modifier = modifier,
        icon = icon,
        contentDescription = text,
        buttonText = text,
        label = if (label) text else null,
        onClick = onClick
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun AudioActionPreview() {
    KaleyraM3Theme {
        Surface {
            AudioAction(AudioDeviceUi.LoudSpeaker, {})
        }
    }
}