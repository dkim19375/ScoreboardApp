package me.dkim19375.scoreboard

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.dkim19375.scoreboard.ui.theme.ScoreboardTheme
import me.dkim19375.scoreboard.util.composable.AutoSizeText

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
            val windowSizeClass = calculateWindowSizeClass(this)
            MainComponent(windowSizeClass)
        }
    }
}

@Composable
fun MainComponent(windowSize: WindowSizeClass) = ScoreboardTheme {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var orientation by remember { mutableStateOf(Configuration.ORIENTATION_PORTRAIT) }
        val configuration = LocalConfiguration.current
        // If our configuration changes then this will launch a new coroutine scope for it
        LaunchedEffect(configuration) {
            // Save any changes to the orientation value on the configuration object
            snapshotFlow { configuration.orientation }.collect { orientation = it }
        }

        var scoreP1 by remember { mutableStateOf(58) }
        var scoreP2 by remember { mutableStateOf(74) }
        val rowColumnContent: @Composable Any.() -> Unit = {
            ScoreBox(
                color = Color.hsv(212f, 0.9f, 0.99f),
                name = "Player 1",
                getScore = { scoreP1 },
                setScore = { scoreP1 = it },
                isTop = true,
                isLandscapeMode = this is RowScope,
                modifier = if (this is ColumnScope) Modifier.weight(1f) else {
                    this as RowScope
                    Modifier.weight(1f)
                },
            )

            Box(
                modifier = if (this is ColumnScope) {
                    Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                } else {
                    Modifier
                        .fillMaxHeight()
                        .width(5.dp)
                }.background(Color.White),
            )

            ScoreBox(
                color = Color.hsv(355f, 0.75f, 0.9f),
                name = "Player 2",
                getScore = { scoreP2 },
                setScore = { scoreP2 = it },
                isTop = false,
                isLandscapeMode = this is RowScope,
                modifier = if (this is ColumnScope) Modifier.weight(1f) else {
                    this as RowScope
                    Modifier.weight(1f)
                },
            )
        }
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                rowColumnContent()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                rowColumnContent()
            }
        }

        // Center - reset button
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            var clickedOnce by remember { mutableStateOf(false) }
            var clickedID by remember { mutableStateOf(0) }
            val coroutineScope = rememberCoroutineScope()
            Button(
                onClick = {
                    if (!clickedOnce) {
                        clickedOnce = true
                        val id = (Int.MIN_VALUE..Int.MAX_VALUE).random()
                        clickedID = id
                        coroutineScope.launch {
                            delay(2000L)
                            if (clickedID == id) {
                                clickedOnce = false
                            }
                        }
                        return@Button
                    }
                    clickedID = 0
                    clickedOnce = false
                    scoreP1 = 0
                    scoreP2 = 0
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    if (clickedOnce) Color(255, 160, 160) else Color.White
                ),
                modifier = Modifier.size(55.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.requiredSize(35.dp)
                )
            }
        }

        // Top right corner - settings button
        Box(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxSize(),
            contentAlignment = Alignment.TopEnd,
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier
                    .padding(top = 40.dp, end = 20.dp)
                    .size(30.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {

                            }
                        )
                    }
                    .clickable {
                        // TODO
                    }
            )
        }
    }
}

@Composable
fun ScoreBox(
    color: Color,
    name: String,
    getScore: () -> Int,
    setScore: (Int) -> Unit,
    isTop: Boolean,
    isLandscapeMode: Boolean,
    modifier: Modifier = Modifier,
) {
    fun increase() {
        setScore(getScore() + 1)
    }

    fun decrease() {
        setScore(getScore() - 1)
    }

    val interactionSource = remember { MutableInteractionSource() }
    var offsetX = remember { 0F }
    var offsetY = remember { 0F }
    var start = remember { 0L }
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = ::increase,
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        offsetX = 0F
                        offsetY = 0F
                        start = System.currentTimeMillis()
                    },
                    onDragEnd = {
                        if (System.currentTimeMillis() - start > 1000L) {
                            return@detectDragGestures
                        }
                        if (offsetY < 0) {
                            increase()
                        } else {
                            decrease()
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.let {
                if (isLandscapeMode) return@let Modifier
                if (isTop) {
                    it.offset(y = (-5).dp)
                } else {
                    it.offset(y = 15.dp)
                }
            }
        ) {
            AutoSizeText(
                text = name,
                minTextSize = 15.sp,
                maxTextSize = 40.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 25.dp)
            )
            AutoSizeText(
                text = getScore().toString(),
                minTextSize = 50.sp,
                maxTextSize = 180.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }
}