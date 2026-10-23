package com.example.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * A SingleChildScrollView composable that provides a vertically scrollable container
 * for a single child component, providing equivalent semantics to Flutter's SingleChildScrollView
 * while utilizing idiomatic Jetpack Compose verticalScroll modifier.
 */
@Composable
fun SingleChildScrollView(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    enabled: Boolean = true,
    testTag: String = "single_child_scroll_view",
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .verticalScroll(state = scrollState, enabled = enabled)
            .testTag(testTag)
    ) {
        content()
    }
}
