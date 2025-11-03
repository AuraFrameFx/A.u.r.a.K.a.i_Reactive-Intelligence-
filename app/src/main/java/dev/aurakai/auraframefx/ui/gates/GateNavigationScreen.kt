package dev.aurakai.auraframefx.ui.gates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Main gate navigation screen with horizontal pager
 * Swipe between module gates and double-tap to enter
 */
@Composable
fun GateNavigationScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val gates = GateConfigs.allGates
    val pagerState = rememberPagerState(pageCount = { gates.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Horizontal pager for gate cards
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val config = gates[page]

            GateCard(
                config = config,
                onDoubleTap = {
                    // Navigate to the module
                    navController.navigate(config.route)
                }
            )
        }

        // Page indicator dots
        GatePageIndicator(
            pageCount = gates.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )

        // Current gate name overlay
        Text(
            text = gates[pagerState.currentPage].title.uppercase(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = gates[pagerState.currentPage].borderColor.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )
    }
}

/**
 * Page indicator dots showing which gate is active
 */
@Composable
private fun GatePageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            val color = if (isActive) Color.Cyan else Color.Gray
            val size = if (isActive) 12.dp else 8.dp

            Box(
                modifier = Modifier
                    .size(size)
                    .background(color.copy(alpha = if (isActive) 1f else 0.4f), shape = androidx.compose.foundation.shape.CircleShape)
            )
        }
    }
}
