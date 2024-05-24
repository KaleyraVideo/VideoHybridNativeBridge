package com.kaleyra.video_sdk.call.stream.view

import android.content.res.Configuration
import android.provider.MediaStore.Video
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.kaleyra.video_sdk.R
import com.kaleyra.video_sdk.call.pointer.view.PointerStreamWrapper
import com.kaleyra.video_sdk.call.stream.model.ImmutableView
import com.kaleyra.video_sdk.call.stream.model.StreamUi
import com.kaleyra.video_sdk.call.stream.model.VideoUi
import com.kaleyra.video_sdk.common.avatar.model.ImmutableUri
import com.kaleyra.video_sdk.common.avatar.view.Avatar
import com.kaleyra.video_sdk.theme.KaleyraM3Theme

internal object StreamItemDefaults {

    val shape = RoundedCornerShape(4.dp)

    val elevation = 1.dp
}

@Composable
internal fun StreamItem(
    modifier: Modifier = Modifier,
    stream: StreamUi,
    fullscreen: Boolean,
    pin: Boolean
) {
    Surface(
        shape = StreamItemDefaults.shape,
        tonalElevation = StreamItemDefaults.elevation,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            PointerStreamWrapper(
                streamView = stream.video?.view,
                pointerList = stream.video?.pointers
            ) { hasPointers ->
                // TODO set up the stream view fill/fit logic
                Stream(
                    streamView = stream.video?.view,
                    avatar = stream.avatar,
                    username = stream.username,
                    showStreamView = stream.video?.view != null && stream.video.isEnabled
                )
            }
            if (fullscreen) {
                FullscreenIcon(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                )
            }
            UserLabel(
                username = stream.username,
                pin = pin,
                modifier = Modifier
                    .height(24.dp)
                    .align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun FullscreenIcon(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.inverseSurface,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_kaleyra_stream_fullscreen_off),
            contentDescription = null,
            modifier = Modifier.padding(3.dp)
        )
    }
}

@Composable
private fun UserLabel(
    modifier: Modifier = Modifier,
    username: String,
    pin: Boolean
) {
    Surface(
        color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = .1f),
        contentColor = MaterialTheme.colorScheme.inverseSurface,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal =  8.dp)
        ) {
            if (pin) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_kaleyra_stream_pin),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .offset(x = (-4).dp)
                )
            }
            Text(
                text = username,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
internal fun StreamItemPreview() {
    KaleyraM3Theme {
        Surface {
            StreamItem(
                stream = StreamUi(
                    id = "id",
                    username = "Viola J. Allen",
                    video = VideoUi(id = "id", view = ImmutableView(View(LocalContext.current))),
                ),
                fullscreen = false,
                pin = false
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
internal fun StreamItemPinnedPreview() {
    KaleyraM3Theme {
        Surface {
            StreamItem(
                stream = StreamUi(
                    id = "id",
                    username = "Viola J. Allen",
                    video = VideoUi(id = "id", view = ImmutableView(View(LocalContext.current))),
                ),
                fullscreen = false,
                pin = true
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
internal fun StreamItemFullscreenPreview() {
    KaleyraM3Theme {
        Surface {
            StreamItem(
                stream = StreamUi(
                    id = "id",
                    username = "Viola J. Allen",
                    video = VideoUi(id = "id", view = ImmutableView(View(LocalContext.current))),
                ),
                fullscreen = true,
                pin = false
            )
        }
    }
}
