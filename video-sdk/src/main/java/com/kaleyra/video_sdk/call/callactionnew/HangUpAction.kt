package com.kaleyra.video_sdk.call.callactionnew

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kaleyra.video_sdk.R
import com.kaleyra.video_sdk.theme.KaleyraM3Theme
import com.kaleyra.video_sdk.theme.KaleyraTheme

@Composable
internal fun HangUpAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CallAction(
        modifier = modifier,
        icon = painterResource(id = R.drawable.ic_kaleyra_call_sheet_hang_up),
        contentDescription = stringResource(id = R.string.kaleyra_call_sheet_hang_up),
        buttonColor = KaleyraTheme.colors.hangUp,
        buttonContentColor = Color.White,
        onClick = onClick
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun HangUpActionPreview() {
    KaleyraM3Theme {
        HangUpAction({})
    }
}