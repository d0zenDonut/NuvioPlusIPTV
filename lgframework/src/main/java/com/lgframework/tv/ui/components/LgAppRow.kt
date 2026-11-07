package com.lgframework.tv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.lgframework.tv.ui.glass.LgCapabilities
import com.lgframework.tv.ui.glass.LgTier
import com.lgframework.tv.ui.tokens.LgMetrics

/**
 * A titled row of glass app tiles. [LgMetrics.GRID_COLUMNS] defines the column count so
 * every row lines up vertically with the rest of the home screen.
 */
@Composable
fun LgAppRow(
    backdrop: Backdrop,
    apps: List<LgApp>,
    modifier: Modifier = Modifier,
    title: String? = null,
    tier: LgTier = LgCapabilities.tier,
) {
    Column(modifier = modifier) {
        if (title != null) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(LgMetrics.TitleToRowSpacing))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            apps.take(LgMetrics.GRID_COLUMNS).forEach { app ->
                LgTile(
                    backdrop = backdrop,
                    app = app,
                    tier = tier,
                    showLabel = true,
                )
            }
        }
    }
}
