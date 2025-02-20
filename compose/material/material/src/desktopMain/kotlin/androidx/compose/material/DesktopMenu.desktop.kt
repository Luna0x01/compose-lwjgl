/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.material

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.contextMenuOpenDetector
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.rememberCursorPositionProvider
import androidx.compose.ui.window.rememberPopupPositionProviderAtPosition

/**
 * A Material Design [dropdown menu](https://material.io/components/menus#dropdown-menu).
 *
 * A [DropdownMenu] behaves similarly to a [Popup], and will use the position of the parent layout
 * to position itself on screen. Commonly a [DropdownMenu] will be placed in a [Box] with a sibling
 * that will be used as the 'anchor'. Note that a [DropdownMenu] by itself will not take up any
 * space in a layout, as the menu is displayed in a separate window, on top of other content.
 *
 * The [content] of a [DropdownMenu] will typically be [DropdownMenuItem]s, as well as custom
 * content. Using [DropdownMenuItem]s will result in a menu that matches the Material
 * specification for menus. Also note that the [content] is placed inside a scrollable [Column],
 * so using a [LazyColumn] as the root layout inside [content] is unsupported.
 *
 * [onDismissRequest] will be called when the menu should close - for example when there is a
 * tap outside the menu, or when the back key is pressed.
 *
 * [DropdownMenu] changes its positioning depending on the available space, always trying to be
 * fully visible. It will try to expand horizontally, depending on layout direction, to the end of
 * its parent, then to the start of its parent, and then screen end-aligned. Vertically, it will
 * try to expand to the bottom of its parent, then from the top of its parent, and then screen
 * top-aligned. An [offset] can be provided to adjust the positioning of the menu for cases when
 * the layout bounds of its parent do not coincide with its visual bounds. Note the offset will
 * be applied in the direction in which the menu will decide to expand.
 *
 * Example usage:
 * @sample androidx.compose.material.samples.MenuSample
 *
 * @param expanded Whether the menu is currently open and visible to the user
 * @param onDismissRequest Called when the user requests to dismiss the menu, such as by
 * tapping outside the menu's bounds
 * @param focusable Whether the dropdown can capture focus
 * @param modifier Modifier for the menu
 * @param offset [DpOffset] to be added to the position of the menu
 * @param content content lambda
 */
@Deprecated(
    level = DeprecationLevel.HIDDEN,
    replaceWith = ReplaceWith(
        expression = "DropdownMenu(expanded,onDismissRequest, focusable, modifier, offset, " +
            "rememberScrollState(), content)",
        "androidx.compose.foundation.rememberScrollState"
    ),
    message = "Replaced by a DropdownMenu function with a ScrollState parameter"
)
@Composable
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    content: @Composable ColumnScope.() -> Unit
) = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    offset = offset,
    scrollState = rememberScrollState(),
    properties = PopupProperties(focusable = focusable),
    content = content
)

/**
 * A Material Design [dropdown menu](https://material.io/components/menus#dropdown-menu).
 *
 * A [DropdownMenu] behaves similarly to a [Popup], and will use the position of the parent layout
 * to position itself on screen. Commonly a [DropdownMenu] will be placed in a [Box] with a sibling
 * that will be used as the 'anchor'. Note that a [DropdownMenu] by itself will not take up any
 * space in a layout, as the menu is displayed in a separate window, on top of other content.
 *
 * The [content] of a [DropdownMenu] will typically be [DropdownMenuItem]s, as well as custom
 * content. Using [DropdownMenuItem]s will result in a menu that matches the Material
 * specification for menus. Also note that the [content] is placed inside a scrollable [Column],
 * so using a [LazyColumn] as the root layout inside [content] is unsupported.
 *
 * [onDismissRequest] will be called when the menu should close - for example when there is a
 * tap outside the menu, or when the back key is pressed.
 *
 * [DropdownMenu] changes its positioning depending on the available space, always trying to be
 * fully visible. It will try to expand horizontally, depending on layout direction, to the end of
 * its parent, then to the start of its parent, and then screen end-aligned. Vertically, it will
 * try to expand to the bottom of its parent, then from the top of its parent, and then screen
 * top-aligned. An [offset] can be provided to adjust the positioning of the menu for cases when
 * the layout bounds of its parent do not coincide with its visual bounds. Note the offset will
 * be applied in the direction in which the menu will decide to expand.
 *
 * Example usage:
 * @sample androidx.compose.material.samples.MenuSample
 *
 * Example usage with a [ScrollState] to control the menu items scroll position:
 * @sample androidx.compose.material.samples.MenuWithScrollStateSample
 *
 * @param expanded Whether the menu is currently open and visible to the user
 * @param onDismissRequest Called when the user requests to dismiss the menu, such as by
 * tapping outside the menu's bounds
 * @param focusable Whether the dropdown can capture focus
 * @param modifier [Modifier] to be applied to the menu's content
 * @param offset [DpOffset] to be added to the position of the menu
 * @param scrollState a [ScrollState] to used by the menu's content for items vertical scrolling
 * @param content the content of this dropdown menu, typically a [DropdownMenuItem]
 */
@Deprecated(
    "Replaced by DropdownMenu with properties parameter",
    ReplaceWith("DropdownMenu(expanded, onDismissRequest, modifier, offset, scrollState," +
        "androidx.compose.ui.window.PopupProperties(focusable = focusable), " +
        "content)"),
    level = DeprecationLevel.HIDDEN
)
@Composable
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
): Unit = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    offset = offset,
    scrollState = scrollState,
    properties = PopupProperties(focusable = focusable),
    content = content
)


// Workaround for `Overload resolution ambiguity` between old and new overload.
@Deprecated("Maintained for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
): Unit = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    modifier = modifier,
    offset = offset,
    scrollState = scrollState,
    properties = PopupProperties(focusable = true),
    content = content
)

/**
 * A variant of a dropdown menu that accepts a [DropdownMenuState] to allow precise positioning.
 *
 * Typically, it should be combined with [Modifier.contextMenuOpenDetector] via state-hoisting.
 *
 * @param state The open/closed state of the menu.
 * @param onDismissRequest Called when the user requests to dismiss the menu, such as by
 * tapping outside the menu's bounds
 *
 */
@Deprecated(
    level = DeprecationLevel.HIDDEN,
    replaceWith = ReplaceWith(
        expression = "DropdownMenu(state, onDismissRequest, focusable, modifier, offset, " +
            "rememberScrollState(), content)",
        "androidx.compose.foundation.rememberScrollState"
    ),
    message = "Replaced by a DropdownMenu function with a ScrollState parameter"
)
@Composable
fun DropdownMenu(
    state: DropdownMenuState,
    onDismissRequest: () -> Unit = { state.status = DropdownMenuState.Status.Closed },
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        state,
        onDismissRequest,
        focusable,
        modifier,
        rememberScrollState(),
        content
    )
}

/**
 * A variant of a dropdown menu that accepts a [DropdownMenuState] to allow precise positioning.
 *
 * Typically, it should be combined with [Modifier.contextMenuOpenDetector] via state-hoisting.
 *
 * Example usage with a [ScrollState] to control the menu items scroll position:
 * @sample androidx.compose.material.samples.MenuWithScrollStateSample
 *
 * @param state The open/closed state of the menu
 * @param onDismissRequest Called when the user requests to dismiss the menu, such as by
 * tapping outside the menu's bounds
 * @param focusable Whether the dropdown can capture focus
 * @param modifier [Modifier] to be applied to the menu's content
 * @param scrollState a [ScrollState] to used by the menu's content for items vertical scrolling
 * @param content the content of this dropdown menu, typically a [DropdownMenuItem]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DropdownMenu(
    state: DropdownMenuState,
    onDismissRequest: () -> Unit = { state.status = DropdownMenuState.Status.Closed },
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    val status = state.status
    var position: Offset? by remember { mutableStateOf(null) }
    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = status is DropdownMenuState.Status.Open

    // Whenever we are asked to open the popup, remember the position
    if (status is DropdownMenuState.Status.Open){
        position = status.position
    }

    if (expandedStates.currentState || expandedStates.targetState) {
        OpenDropdownMenu(
            expandedStates = expandedStates,
            popupPositionProvider = rememberPopupPositionProviderAtPosition(position!!),
            scrollState = scrollState,
            onDismissRequest = onDismissRequest,
            focusable = focusable,
            modifier = modifier,
            content = content
        )
    }
}

/**
 * The implementation of a [DropdownMenu] in its open state.
 */
@Composable
private fun OpenDropdownMenu(
    expandedStates: MutableTransitionState<Boolean>,
    popupPositionProvider: PopupPositionProvider,
    transformOriginState: MutableState<TransformOrigin> =
        remember { mutableStateOf(TransformOrigin.Center) },
    scrollState: ScrollState,
    onDismissRequest: () -> Unit,
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    var focusManager: FocusManager? by remember { mutableStateOf(null) }
    var inputModeManager: InputModeManager? by remember { mutableStateOf(null) }
    Popup(
        onDismissRequest = onDismissRequest,
        popupPositionProvider = popupPositionProvider,
        properties = PopupProperties(focusable = focusable),
        onKeyEvent = {
            handlePopupOnKeyEvent(it, focusManager!!, inputModeManager!!)
        },
    ) {
        focusManager = LocalFocusManager.current
        inputModeManager = LocalInputModeManager.current

        DropdownMenuContent(
            expandedStates = expandedStates,
            transformOriginState = transformOriginState,
            scrollState = scrollState,
            modifier = modifier,
            content = content
        )
    }
}

/**
 * <a href="https://material.io/components/menus#dropdown-menu" class="external" target="_blank">Material Design dropdown menu</a> item.
 *
 *
 * Example usage:
 * @sample androidx.compose.material.samples.MenuSample
 *
 * @param onClick Called when the menu item was clicked
 * @param modifier The modifier to be applied to the menu item
 * @param enabled Controls the enabled state of the menu item - when `false`, the menu item
 * will not be clickable and [onClick] will not be invoked
 * @param contentPadding the padding applied to the content of this menu item
 * @param interactionSource the [MutableInteractionSource] representing the stream of
 * [Interaction]s for this DropdownMenuItem. You can create and pass in your own remembered
 * [MutableInteractionSource] if you want to observe [Interaction]s and customize the
 * appearance / behavior of this DropdownMenuItem in different [Interaction]s.
 */
@Composable
actual fun DropdownMenuItem(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource,
    content: @Composable RowScope.() -> Unit
) {
    DropdownMenuItemContent(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * A [CursorDropdownMenu] behaves similarly to [Popup] and will use the current position of the mouse
 * cursor to position itself on screen.
 *
 * The [content] of a [CursorDropdownMenu] will typically be [DropdownMenuItem]s, as well as custom
 * content. Using [DropdownMenuItem]s will result in a menu that matches the Material
 * specification for menus.
 *
 * @param expanded Whether the menu is currently open and visible to the user
 * @param onDismissRequest Called when the user requests to dismiss the menu, such as by
 * tapping outside the menu's bounds
 * @param focusable Sets the ability for the menu to capture focus
 * @param modifier The modifier for this layout.
 * @param content The content lambda.
 */
@Deprecated(
    level = DeprecationLevel.HIDDEN,
    replaceWith = ReplaceWith(
        expression = "CursorDropdownMenu(expanded, onDismissRequest, focusable, modifier, " +
            "rememberScrollState(), content)",
        "androidx.compose.foundation.rememberScrollState"
    ),
    message = "Replaced by a CursorDropdownMenu function with a ScrollState parameter"
)
@Composable
fun CursorDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) = CursorDropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    focusable = focusable,
    modifier = modifier,
    scrollState = rememberScrollState(),
    content = content
)

/**
 * A [CursorDropdownMenu] behaves similarly to [Popup] and will use the current position of the mouse
 * cursor to position itself on screen.
 *
 * The [content] of a [CursorDropdownMenu] will typically be [DropdownMenuItem]s, as well as custom
 * content. Using [DropdownMenuItem]s will result in a menu that matches the Material
 * specification for menus.
 *
 * @param expanded Whether the menu is currently open and visible to the user
 * @param onDismissRequest Called when the user requests to dismiss the menu, such as by
 * tapping outside the menu's bounds
 * @param focusable Whether the dropdown can capture focus
 * @param modifier [Modifier] to be applied to the menu's content
 * @param scrollState a [ScrollState] to used by the menu's content for items vertical scrolling
 * @param content the content of this dropdown menu, typically a [DropdownMenuItem]
 */
@Composable
fun CursorDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    focusable: Boolean = true,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded

    if (expandedStates.currentState || expandedStates.targetState) {
        OpenDropdownMenu(
            expandedStates = expandedStates,
            popupPositionProvider = rememberCursorPositionProvider(),
            scrollState = scrollState,
            onDismissRequest = onDismissRequest,
            focusable = focusable,
            modifier = modifier,
            content = content
        )
    }
}

/**
 * Represents the open/closed state of a dropdown menu.
 */
@Stable
class DropdownMenuState(initialStatus: Status = Status.Closed) {

    /**
     * The current status of the menu.
     */
    var status: Status by mutableStateOf(initialStatus)

    @Immutable
    sealed class Status {

        class Open(val position: Offset) : Status() {

            override fun equals(other: Any?): Boolean {
                if (this === other)
                    return true

                if (other !is Open)
                    return false

                if (position != other.position)
                    return false

                return true
            }

            override fun hashCode(): Int {
                return position.hashCode()
            }

            override fun toString(): String {
                return "Open(position=$position)"
            }
        }

        object Closed : Status()

    }

}

/**
 * A [Modifier] that detects events that should typically open a context menu (mouse right-clicks)
 * and modify the given [DropdownMenuState] accordingly.
 */
@ExperimentalMaterialApi
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.contextMenuOpenDetector(
    state: DropdownMenuState,
    enabled: Boolean = true,
): Modifier {
    return if (enabled) {
        this.contextMenuOpenDetector(
            key = state,
            enabled = state.status is DropdownMenuState.Status.Closed
        ) { pointerPosition ->
            state.status = DropdownMenuState.Status.Open(pointerPosition)
        }
    } else {
        this
    }
}
