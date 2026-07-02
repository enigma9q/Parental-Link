package com.enigma.familylinklite

import android.os.Bundle
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * First Kotlin/Compose surface.
 *
 * Core app logic, services, protocol and legacy screens remain in Java.
 * Only the parent dashboard is replaced here. Other screens still use LegacyMainActivity.
 */
open class ComposeMainActivity : LegacyMainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun showConnectedParent(autoRefresh: Boolean) {
        composePrepareParentDashboard()
        setContentView(
            ComposeView(this).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setContent { ParentDashboard() }
            }
        )
        if (autoRefresh) {
            composeScheduleAutoRefresh()
        }
    }

    @Composable
    private fun ParentDashboard() {
        val refreshTick = remember { mutableStateOf(0) }
        val disabled = composeDisabled()
        val timeout = composeTimeoutActive()
        val request = composeRequestPending()
        val bg = Color(0xFF071014)
        val card = Color(0xFF151B22)
        val card2 = Color(0xFF1A222C)
        val blue = Color(0xFF1683FF)
        val red = Color(0xFFFF5065)
        val green = Color(0xFF35D061)
        val muted = Color(0xFFAAB2BD)
        val text = Color(0xFFF4F7FA)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            TopBar(card2, blue, text) { composeMenu() }
            Spacer(Modifier.height(10.dp))
            ChildStatusCard(card, card2, blue, green, red, muted, text)
            Spacer(Modifier.height(8.dp))
            RequestCard(card, request, red, blue, muted, text)
            Spacer(Modifier.height(8.dp))
            ActionsCard(card, blue, red, text, disabled, timeout)
            Spacer(Modifier.height(8.dp))
            ActivityCard(card, blue, muted, text)
            Spacer(Modifier.height(8.dp))
            BottomBar(card2, text, muted, onRefresh = {
                composeRefresh()
                refreshTick.value++
            })
            Spacer(Modifier.weight(1f))
        }
    }

    @Composable
    private fun TopBar(card: Color, blue: Color, text: Color, onMenu: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(blue)
                    .clickable { composeRefresh() },
                contentAlignment = Alignment.Center
            ) {
                Text("PL", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(14.dp))
            Text(
                "Parental-Link",
                color = text,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(card)
                    .clickable { onMenu() }
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Menu •", color = text, fontSize = 18.sp)
            }
        }
    }

    @Composable
    private fun ChildStatusCard(
        card: Color,
        chip: Color,
        blue: Color,
        green: Color,
        red: Color,
        muted: Color,
        text: Color
    ) {
        val statusColor = when {
            composeIsOnline() -> green
            composeIsStale() -> red
            else -> chip
        }
        val accessTitle = when {
            composeTimeoutActive() -> "Timeout active"
            composeDisabled() -> "Device blocked"
            else -> "Ready"
        }
        val accessSub = when {
            composeTimeoutActive() || composeDisabled() -> "Limited access"
            else -> "Normal access"
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(card)
                .border(1.dp, Color(0xFF2B3541), RoundedCornerShape(22.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22344D)),
                contentAlignment = Alignment.Center
            ) {
                Text("👦", fontSize = 38.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        composeChildName(),
                        color = text,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    StatusPill(composeStatusLabel(), statusColor)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    MiniMetric("▣", "${composeBattery()}%", muted)
                    MiniMetric("⌁", "Wi‑Fi", muted)
                }
                Spacer(Modifier.height(8.dp))
                InfoLine("▶", composeCurrentApp(), "Current app", text, muted)
                InfoLine(if (composeTimeoutActive()) "⏳" else if (composeDisabled()) "🔒" else "✓", accessTitle, accessSub, text, muted)
            }
        }
    }

    @Composable
    private fun StatusPill(label: String, color: Color) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(color)
                .padding(horizontal = 10.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = Color.White, fontSize = 12.sp, maxLines = 1)
        }
    }

    @Composable
    private fun MiniMetric(icon: String, value: String, muted: Color) {
        Text("$icon  $value", color = muted, fontSize = 13.sp, maxLines = 1)
    }

    @Composable
    private fun InfoLine(icon: String, title: String, subtitle: String, text: Color, muted: Color) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
            Text(icon, color = text, fontSize = 17.sp, modifier = Modifier.width(26.dp))
            Column {
                Text(title, color = text, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = muted, fontSize = 12.sp, maxLines = 1)
            }
        }
    }

    @Composable
    private fun RequestCard(card: Color, pending: Boolean, red: Color, blue: Color, muted: Color, text: Color) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(if (pending) Color(0xFF2A161C) else card)
                .border(1.dp, if (pending) red else Color(0xFF2B3541), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Text(if (pending) "Unlock request" else "Requests", color = if (pending) red else text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            if (pending) {
                Text("Child asked to unlock: ${composeRequestReason()}", color = muted, fontSize = 13.sp)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    RequestButton("Allow now", blue, Modifier.weight(1f)) { composeAllowRequest() }
                    RequestButton("Snooze 15 min", Color(0xFF202832), Modifier.weight(1f)) { composeSnoozeRequest() }
                }
            } else {
                Text("Requests will be visible here.", color = muted, fontSize = 14.sp)
            }
        }
    }

    @Composable
    private fun RequestButton(label: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier
                .height(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    private fun ActionsCard(card: Color, blue: Color, red: Color, text: Color, disabled: Boolean, timeout: Boolean) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(card)
                .border(1.dp, Color(0xFF2B3541), RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Actions", color = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("More", color = blue, fontSize = 15.sp, modifier = Modifier.clickable { composeMore() })
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                ActionTile(if (disabled) "⏻" else "🔒", if (disabled) "Enable" else "Block", if (disabled) blue else red, Modifier.weight(1f)) { composeToggleBlock() }
                ActionTile(if (timeout) "■" else "⏳", if (timeout) "Stop" else "Timeout", blue, Modifier.weight(1f)) { composeToggleTimeout() }
                ActionTile("🔊", "Sound", blue, Modifier.weight(1f)) { composeSound() }
                ActionTile("📣", "Ring", blue, Modifier.weight(1f)) { composeRing() }
            }
        }
    }

    @Composable
    private fun ActionTile(icon: String, label: String, border: Color, modifier: Modifier, onClick: () -> Unit) {
        Column(
            modifier = modifier
                .height(66.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF18212B))
                .border(1.5.dp, border, RoundedCornerShape(18.dp))
                .clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 18.sp)
            Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }

    @Composable
    private fun ActivityCard(card: Color, blue: Color, muted: Color, text: Color) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(card)
                .border(1.dp, Color(0xFF2B3541), RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Activity", color = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("View all", color = blue, fontSize = 15.sp, modifier = Modifier.clickable { composeActivity() })
            }
            Spacer(Modifier.height(8.dp))
            composeActivityRows().forEach { row ->
                Text(row, color = muted, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(vertical = 1.dp))
            }
        }
    }

    @Composable
    private fun BottomBar(card: Color, text: Color, muted: Color, onRefresh: () -> Unit) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            BottomButton("↻", "Refresh", card, text, Modifier.weight(1f)) { onRefresh() }
            BottomButton("▦", "Devices", card, text, Modifier.weight(1f)) { composeDevices() }
            BottomButton("⚙", "Interface", card, text, Modifier.weight(1f)) { composeInterface() }
        }
    }

    @Composable
    private fun BottomButton(icon: String, label: String, color: Color, text: Color, modifier: Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier
                .height(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("$icon  $label", color = text, fontSize = 13.sp)
        }
    }
}
