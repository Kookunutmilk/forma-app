package com.forma.app.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forma.app.designsystem.FormaBackground
import com.forma.app.designsystem.FormaLime
import com.forma.app.designsystem.FormaMutedSoft

@Composable
fun FormaBottomBar(
    currentRoute: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.28f to FormaBackground.copy(alpha = 0.94f),
                    0.5f to FormaBackground,
                    1f to FormaBackground,
                ),
            )
            .navigationBarsPadding()
            .padding(start = 4.dp, end = 4.dp, top = 22.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        bottomTabs.forEach { tab ->
            val selected = currentRoute == tab.route
            val alpha by animateFloatAsState(if (selected) 1f else 0.55f, label = "tab-alpha")
            val interaction = remember { MutableInteractionSource() }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        onClick = { onSelect(tab.route) },
                    )
                    .padding(vertical = 4.dp)
                    .alpha(alpha),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.icon,
                    contentDescription = tab.label,
                    tint = if (selected) FormaLime else FormaMutedSoft,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                    color = if (selected) FormaLime else FormaMutedSoft,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
