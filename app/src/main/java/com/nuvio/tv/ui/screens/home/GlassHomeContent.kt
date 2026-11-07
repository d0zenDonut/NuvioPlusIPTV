package com.nuvio.tv.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.lgframework.tv.ui.components.LgApp
import com.lgframework.tv.ui.components.LgDock
import com.lgframework.tv.ui.components.LgTile
import com.lgframework.tv.ui.tokens.LgMetrics
import com.nuvio.tv.domain.model.CatalogRow
import com.nuvio.tv.domain.model.MetaPreview
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import com.lgframework.tv.ui.components.LgAppGlyph
import com.lgframework.tv.ui.tokens.LgShapes
import kotlinx.coroutines.launch

/**
 * tvOS-27 liquid-glass home.
 *
 * Structure is driven by one hard rule of Backdrop: a glass surface can only refract what
 * was recorded into its layer. So the wallpaper (image + scrim) is drawn once, wrapped in
 * `Modifier.layerBackdrop(backdrop)`, and every glass surface afterwards samples that same
 * recorded layer. Nothing glass may be nested inside a recorded subtree, or it refracts
 * itself.
 *
 * Data is unchanged: rows come straight from [HomeUiState.catalogRows], produced by the
 * existing catalog pipeline, so posters and trailers keep flowing as before.
 */
@Composable
fun GlassHomeContent(
    uiState: HomeUiState,
    onNavigateToDetail: (String, String, String) -> Unit,
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backdrop = rememberLayerBackdrop()

    val rows = remember(uiState.catalogRows) {
        uiState.catalogRows.filter { it.items.isNotEmpty() }
    }

    // Lazy list keys must be unique or Compose throws "Key ... was already used". A catalog id
    // alone is not unique - Cinemeta serves "top" for both movie and series - and the pipeline
    // can surface the same catalog twice, so this follows the app's own [catalogRowKey] and
    // falls back to an occurrence suffix on the (rare) remaining collision.
    val keyedRows = remember(rows) {
        val seen = HashMap<String, Int>(rows.size)
        rows.map { row ->
            val base = catalogRowKey(row)
            val occurrence = (seen[base] ?: 0) + 1
            seen[base] = occurrence
            GlassKeyedRow(row, if (occurrence == 1) base else base + '#' + occurrence)
        }
    }
    val hero = uiState.heroItems.firstOrNull()
        ?: rows.firstOrNull()?.items?.firstOrNull()

    // Row-to-row focus is driven explicitly: a nested LazyRow cannot hand focus to a LazyColumn
    // item that has not been composed yet, which is why D-pad Down used to stop on row 1.
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val rowFocusRequesters = remember(keyedRows.size) {
        List(keyedRows.size) { FocusRequester() }
    }
    var focusedRowIndex by remember { mutableIntStateOf(0) }
    val rowItemOffset = if (hero != null) 1 else 0

    // Dock icons are quick-jump shortcuts to the top catalogs; the routing payload is kept
    // beside the presentation model rather than inside it.
    // One dock slot per distinct catalog. Cinemeta serves "Popular" for both movie and series,
    // which would otherwise render two identical glyphs side by side.
    val dockEntries = remember(rows) {
        rows.distinctBy { it.catalogName.trim().lowercase() }
            .take(7)
            .mapIndexed { index, row ->
                // Dock icons are real artwork, not letter tiles: the reference shows bare
                // icons sitting on the bar.
                val art = row.items.firstOrNull()?.let { it.backdropUrl ?: it.poster }
                LgDockEntry(row.toDockApp(index), row.toTarget(), art)
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Wallpaper and scrim are recorded together so the glass refracts exactly what is on
        // screen - scrimming after the fact would make the glass read too bright.
        Box(
            Modifier
                .fillMaxSize()
                .layerBackdrop(backdrop)
        ) {
            GlassWallpaper(hero)
        }

        LazyColumn(
            state = listState,
            // The dock floats over the wallpaper. Ending the viewport above it keeps row 2 from
            // scrolling under the bar, where its labels collided with the glass.
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = LgMetrics.DockHeight + 52.dp)
                .onPreviewKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
                    val target = when (event.key) {
                        Key.DirectionDown -> focusedRowIndex + 1
                        Key.DirectionUp -> focusedRowIndex - 1
                        else -> return@onPreviewKeyEvent false
                    }
                    if (target !in keyedRows.indices) return@onPreviewKeyEvent false
                    focusedRowIndex = target
                    // Scroll the target row in first, then move focus to its first tile.
                    scope.launch {
                        runCatching { listState.animateScrollToItem(rowItemOffset + target) }
                        withFrameNanos { }
                        runCatching { rowFocusRequesters[target].requestFocus() }
                    }
                    true
                },
            contentPadding = PaddingValues(
                start = LgMetrics.ScreenPadding,
                end = LgMetrics.ScreenPadding,
                top = 56.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(LgMetrics.RowSpacing)
        ) {
            if (hero != null) {
                item(key = "hero") {
                    GlassHeroHeader(hero)
                }
            }

            itemsIndexed(keyedRows, key = { _, keyed -> keyed.key }) { index, keyed ->
                GlassCatalogRow(
                    row = keyed.row,
                    firstTileFocusRequester = rowFocusRequesters[index],
                    onRowFocused = { focusedRowIndex = index },
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToCatalogSeeAll = onNavigateToCatalogSeeAll
                )
            }
        }

        LgDock(
            backdrop = backdrop,
            apps = dockEntries.map { it.app },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp),
            onAppClick = { app ->
                dockEntries.firstOrNull { it.app.id == app.id }?.target?.let { target ->
                    onNavigateToCatalogSeeAll(target.catalogId, target.addonId, target.apiType)
                }
            },
            icon = { app, size ->
                val art = dockEntries.firstOrNull { it.app.id == app.id }?.art
                if (art == null) {
                    LgAppGlyph(tint = app.tint, initial = app.initial, size = size)
                } else {
                    AsyncImage(
                        model = art,
                        contentDescription = app.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(size)
                            .clip(LgShapes.glyph(size))
                    )
                }
            }
        )
    }
}

/** Presentation model plus the routing payload its dock icon launches. */
private data class LgDockEntry(val app: LgApp, val target: LgCatalogTarget, val art: String?)

/** A catalog row paired with its unique LazyColumn key. */
private data class GlassKeyedRow(val row: CatalogRow, val key: String)

/** A poster paired with its unique LazyRow key. */
private data class GlassKeyedItem(val item: MetaPreview, val key: String)

private data class LgCatalogTarget(
    val catalogId: String,
    val addonId: String,
    val apiType: String
)

private val GlassDockTints = listOf(
    Color(0xFF3B6CFF),
    Color(0xFFFF5A5F),
    Color(0xFF8B5CF6),
    Color(0xFF34C759),
    Color(0xFFFF9F0A),
    Color(0xFF00D4A0),
    Color(0xFFB45AF5)
)

private fun tintFor(key: String): Color {
    val hash = key.hashCode()
    val index = (if (hash < 0) -(hash + 1) else hash) % GlassDockTints.size
    return GlassDockTints[index]
}

private fun CatalogRow.toTarget(): LgCatalogTarget =
    LgCatalogTarget(catalogId = catalogId, addonId = addonId, apiType = apiType)

private fun CatalogRow.toDockApp(index: Int): LgApp = LgApp(
    id = catalogId + '|' + addonId + '|' + apiType,
    name = catalogName,
    // Two letters, matching the framework's own app-icon convention (see LgDemoApps).
    initial = catalogName.trim().take(2).uppercase().ifBlank { "?" },
    tint = GlassDockTints[index % GlassDockTints.size]
)

/** Full-bleed hero art plus a bottom-up scrim so overlay text stays legible. */
@Composable
private fun GlassWallpaper(hero: MetaPreview?) {
    val url = hero?.backdropUrl

    if (url != null) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0.0f to Color.Black.copy(alpha = 0.15f),
                    0.45f to Color.Black.copy(alpha = 0.35f),
                    1.0f to Color.Black.copy(alpha = 0.88f)
                )
            )
    )
}

@Composable
private fun GlassHeroHeader(hero: MetaPreview) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = hero.name,
            color = Color.White,
            fontSize = 46.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        val subtitle = listOfNotNull(
            hero.releaseInfo?.takeIf { it.isNotBlank() },
            hero.imdbRating?.let { "%.1f".format(it) },
            hero.genres.take(3).takeIf { it.isNotEmpty() }?.joinToString("  -  ")
        ).joinToString("  -  ")

        if (subtitle.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }

        hero.description?.takeIf { it.isNotBlank() }?.let { description ->
            Spacer(Modifier.height(12.dp))
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.62f)
            )
        }
    }
}

@Composable
private fun GlassCatalogRow(
    row: CatalogRow,
    firstTileFocusRequester: FocusRequester,
    onRowFocused: () -> Unit,
    onNavigateToDetail: (String, String, String) -> Unit,
    onNavigateToCatalogSeeAll: (String, String, String) -> Unit
) {
    val backdrop = rememberLayerBackdrop()

    // Same duplicate-key hazard as the row list: a row can legitimately carry the same title
    // twice, so keys carry an occurrence suffix.
    val keyedItems = remember(row.items) {
        val seen = HashMap<String, Int>(row.items.size)
        row.items.map { item ->
            val occurrence = (seen[item.id] ?: 0) + 1
            seen[item.id] = occurrence
            GlassKeyedItem(item, if (occurrence == 1) item.id else item.id + '#' + occurrence)
        }
    }

    Column(Modifier.fillMaxWidth()) {
        // The row title is recorded too, so the tiles below refract it at their edge.
        Box(
            Modifier
                .fillMaxWidth()
                .layerBackdrop(backdrop)
        ) {
            Text(
                text = row.catalogName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(LgMetrics.TitleToRowSpacing))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            itemsIndexed(keyedItems, key = { _, entry -> entry.key }) { index, entry ->
                val item = entry.item
                LgTile(
                    backdrop = backdrop,
                    app = item.toTileApp(row),
                    showLabel = true,
                    onFocusChange = { focused -> if (focused) onRowFocused() },
                    modifier = Modifier
                        .then(
                            if (index == 0) Modifier.focusRequester(firstTileFocusRequester)
                            else Modifier
                        )
                        .clickable {
                            onNavigateToDetail(item.id, item.apiType, row.addonBaseUrl)
                        },
                    glyph = {
                        AsyncImage(
                            model = item.backdropUrl ?: item.poster,
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        )
                    }
                )
            }

            item(key = "seeAll") {
                GlassSeeAllTile(
                    backdrop = backdrop,
                    onClick = {
                        onNavigateToCatalogSeeAll(row.catalogId, row.addonId, row.apiType)
                    }
                )
            }
        }
    }
}

private fun MetaPreview.toTileApp(row: CatalogRow): LgApp = LgApp(
    id = id,
    name = name,
    initial = name.take(1).uppercase().ifBlank { "?" },
    tint = tintFor(row.catalogId + '|' + id)
)

@Composable
private fun GlassSeeAllTile(
    backdrop: Backdrop,
    onClick: () -> Unit
) {
    LgTile(
        backdrop = backdrop,
        app = LgApp(
            id = "__see_all__",
            name = "See All",
            initial = ">>",
            tint = Color.White.copy(alpha = 0.85f)
        ),
        showLabel = true,
        modifier = Modifier.clickable(onClick = onClick)
    )
}
