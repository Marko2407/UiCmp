package com.mvukosav.uicmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.mvukosav.uicmp.ui.theme.UiCmpTheme
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UiCmpTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                            .padding(vertical = 16.dp, horizontal = 24.dp)
                    ) {
                        TwoButtonsRow(
                            leftText = "Super long left button label that should ellipsize nicely at the end",
                            rightText = "Action for super",
                            onLeftClick = {},
                            onRightClick = {},
                        )
                        ButtonIconButtonRow(
                            leftText = "Very very long title that should truncate with an ellipsis",
                            rightText = "Action for su",
                            icon = Icons.Default.Star,
                            onLeftClick = {},
                            onRightClick = {},
                        )

                        ButtonIconButtonConstraintRow(
                            leftText = "Very very long title that should truncate with an ellipsis",
                            rightText = "Action for su",
                            icon = Icons.Default.Star,
                            onLeftClick = {},
                            onRightClick = {},
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("This is the center")
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = "Centered star",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TwoButtonsRow(
    leftText: String,
    rightText: String,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    modifier: Modifier = Modifier,
    leftEnabled: Boolean = true,
    rightEnabled: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onLeftClick,
            enabled = leftEnabled,
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minWidth = 0.dp),
        ) {
            Text(
                text = leftText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = onRightClick,
            enabled = rightEnabled
        ) {
            Text(text = rightText)
        }
    }
}

@Composable
fun ButtonIconButtonConstraintRow(
    leftText: String,
    rightText: String,
    icon: ImageVector,
    iconContentDescription: String? = null,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    iconMaxHeight: Dp = 32.dp
) {
    ConstraintLayout(modifier = modifier) {
        val (leftBtn, centerIcon, rightBtn) = createRefs()

        Icon(
            imageVector = icon,
            contentDescription = iconContentDescription,
            modifier = Modifier
                .constrainAs(centerIcon) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
                .heightIn(max = iconMaxHeight)
        )

        Button(
            onClick = onLeftClick,
            modifier = Modifier.constrainAs(leftBtn) {
                start.linkTo(parent.start)
                end.linkTo(centerIcon.start, margin = spacing)
                centerVerticallyTo(parent)
                width = Dimension.preferredWrapContent
            }
        ) {
            Text(
                text = leftText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = false
            )
        }

        Button(
            onClick = onRightClick,
            modifier = Modifier.constrainAs(rightBtn) {
                start.linkTo(centerIcon.end, margin = spacing)
                end.linkTo(parent.end)
                centerVerticallyTo(parent)
                width = Dimension.wrapContent
                horizontalBias = 1f //to keep button fully to the right
            }
        ) {
            Text(
                text = rightText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = false
            )
        }
    }
}


@Composable
fun ButtonIconButtonRow(
    leftText: String,
    rightText: String,
    icon: ImageVector,
    iconContentDescription: String? = null,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    iconMaxHeight: Dp = 32.dp
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)

        val iconPlaceable = subcompose("icon") {
            Icon(
                imageVector = icon,
                contentDescription = iconContentDescription,
                modifier = Modifier.heightIn(max = iconMaxHeight)
            )
        }.first().measure(loose)

        val spacingPx = spacing.roundToPx()
        val maxW = constraints.maxWidth
        val iconW = iconPlaceable.width

        val halfAvailable = ((maxW - iconW) / 2).coerceAtLeast(0)
        val sideMaxWidth = max(0, halfAvailable - spacingPx)

        val leftPlaceable = subcompose("left") {
            Button(
                onClick = onLeftClick,
                modifier = Modifier
                    .widthIn(max = Dp.Unspecified)
            ) {
                Text(
                    text = leftText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
        }.first().measure(
            loose.copy(maxWidth = sideMaxWidth)
        )

        val rightPlaceable = subcompose("right") {
            Button(onClick = onRightClick) {
                Text(
                    text = rightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
        }.first().measure(
            loose.copy(maxWidth = sideMaxWidth)
        )

        val height = listOf(
            leftPlaceable.height, iconPlaceable.height, rightPlaceable.height
        ).maxOrNull()?.coerceAtLeast(constraints.minHeight) ?: constraints.minHeight

        layout(width = maxW, height = height) {
            val iconX = (maxW - iconW) / 2
            val iconY = (height - iconPlaceable.height) / 2
            iconPlaceable.placeRelative(iconX, iconY)

            val leftY = (height - leftPlaceable.height) / 2
            leftPlaceable.placeRelative(0, leftY)

            val rightX = maxW - rightPlaceable.width
            val rightY = (height - rightPlaceable.height) / 2
            rightPlaceable.placeRelative(rightX, rightY)
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun TwoButtonsRowPreview() {
    MaterialTheme {
        TwoButtonsRow(
            leftText = "Super long left button label that should ellipsize nicely at the end",
            rightText = "Action",
            onLeftClick = {},
            onRightClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun TwoButtonsWithIconRowPreview() {
    MaterialTheme {
        ButtonIconButtonRow(
            leftText = "Very very long title that should truncate with an ellipsis",
            rightText = "Action",
            icon = Icons.Default.Star,
            onLeftClick = {},
            onRightClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun TwoButtonsWithIconInConstraintLayoutPreview() {
    MaterialTheme {
        ButtonIconButtonConstraintRow(
            leftText = "Very very long title that should truncate with an ellipsis",
            rightText = "Action for su",
            icon = Icons.Default.Star,
            onLeftClick = {},
            onRightClick = {},
        )
    }
}