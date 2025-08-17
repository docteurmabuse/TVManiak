package com.tizzone.tvmaniak.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.tizzone.tvmaniak.resources.Res
import com.tizzone.tvmaniak.resources.search
import com.tizzone.tvmaniak.resources.search_default_placeholder
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvManiakTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    backgroundColor: Color = Color.Transparent,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors =
            TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = backgroundColor,
            ),
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvManiakSearchTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    backgroundColor: Color = Color.Transparent,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onSearch: (String) -> Unit = {},
    searchPlaceholder: String? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    CenterAlignedTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors =
            TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = backgroundColor,
            ),
        title = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = searchPlaceholder ?: stringResource(Res.string.search_default_placeholder),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                },
                textStyle = MaterialTheme.typography.bodySmall,
                shape = RoundedCornerShape(8.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions =
                    KeyboardActions(
                        onSearch = {
                            onSearch(searchQuery)
                            keyboardController?.hide()
                        },
                    ),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(Res.string.search)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onSearchQueryChange("")
                            },
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear search",
                            )
                        }
                    }
                },
            )
        },
    )
}
