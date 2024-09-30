// File: app/src/main/java/fr/eseo/ld/android/cp/colour/ui/WhatColourApp.kt

package fr.eseo.ld.android.cp.colour.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import fr.eseo.ld.android.cp.colour.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import fr.eseo.ld.android.cp.colour.model.WhatColourDataStore
import fr.eseo.ld.android.cp.colour.service.ChangeLanguage
import fr.eseo.ld.android.cp.colour.ui.screens.GameScreen
import fr.eseo.ld.android.cp.colour.ui.screens.WelcomeScreen
import fr.eseo.ld.android.cp.colour.ui.viewmodels.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatColourAppBar(
    currentScreen: WhatColourScreens,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun WhatColourApp(
    navController: NavHostController = rememberNavController()
) {

    val context = LocalContext.current
    val dataStore = remember { WhatColourDataStore(context) }
    val viewModel: GameViewModel = viewModel(factory = GameViewModel.GameViewModelFactory(dataStore))



    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = WhatColourScreens.valueOf(
        backStackEntry?.destination?.route ?: WhatColourScreens.WELCOME.name
    )

    Scaffold(
        topBar = {
            WhatColourAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = WhatColourScreens.WELCOME.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(
                route = WhatColourScreens.WELCOME.name
            ) {
                WelcomeScreen(
                    viewModel = viewModel,
                    playGameClick = {
                        viewModel.startGame()
                        navController.navigate(WhatColourScreens.GAME.name)
                    }
                )
            }
            composable(
                route = WhatColourScreens.GAME.name
            ) {
                GameScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
        }
    }

}