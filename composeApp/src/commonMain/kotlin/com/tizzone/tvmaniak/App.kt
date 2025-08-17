package com.tizzone.tvmaniak

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.tizzone.tvmaniak.model.TvManiakBottomNavigation
import com.tizzone.tvmaniak.navigation.TvManiakNavGraph
import com.tizzone.tvmaniak.theme.TVManiakExpressiveTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    windowSizeClass: WindowSizeClass,
) {
    TVManiakExpressiveTheme(
        seedColor = MaterialTheme.colorScheme.primary,
    ) {
        val navController = rememberNavController()
        val startDestination = TvManiakBottomNavigation.HOME
        var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

        Scaffold(
            modifier =
                Modifier
                    .fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 8.dp,
                ) {
                    TvManiakBottomNavigation.entries.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            modifier = Modifier,
                            selected = selectedDestination == index,
                            onClick = {
                                selectedDestination = index
                                when (destination.route) {
                                    is String -> {}
                                    // Skip unimplemented routes
                                    else -> {
                                        navController.navigate(destination.route)
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = (
                                        if (selectedDestination == index) {
                                            destination.selectedIcon
                                        } else {
                                            destination.unselectedIcon
                                        }
                                    ),
                                    contentDescription = stringResource(destination.titleResource),
                                )
                            },
                            label = { Text(text = stringResource(destination.titleResource)) },
                        )
                    }
                }
            },
        ) { paddingValues ->
            TvManiakNavGraph(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                navController = navController,
                windowSizeClass = windowSizeClass
            )
        }
    }
}
