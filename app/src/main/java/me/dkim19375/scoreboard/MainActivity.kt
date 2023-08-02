package me.dkim19375.scoreboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.dkim19375.scoreboard.ui.screen.ScoreboardScreen
import me.dkim19375.scoreboard.ui.screen.SettingsScreen
import me.dkim19375.scoreboard.ui.theme.ScoreboardTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Remember a SystemUiController
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = !isSystemInDarkTheme()

            DisposableEffect(systemUiController, useDarkIcons) {
                // Update all of the system bar colors to be transparent, and use
                // dark icons if we're in light theme
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = useDarkIcons,
                    isNavigationBarContrastEnforced = false
                )
                // setStatusBarColor() and setNavigationBarColor() also exist

                onDispose {}
            }
            MainApp()
        }
    }
}

@Composable
fun MainApp(navController: NavHostController = rememberNavController()) = ScoreboardTheme {
    NavHost(
        navController = navController,
        startDestination = Screen.Scoreboard.name,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.Scoreboard.name) {
            ScoreboardScreen(navController)
        }

        composable(Screen.Settings.name) {
            SettingsScreen(navController)
        }
    }
}