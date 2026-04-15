package com.ielts.vocab.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ielts.vocab.ui.components.CircularProgressWithText
import com.ielts.vocab.ui.theme.Blue40
import com.ielts.vocab.ui.theme.GreenSuccess
import com.ielts.vocab.ui.theme.RedError
import com.ielts.vocab.ui.theme.YellowWarning
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToStudy: () -> Unit,
    onNavigateToReview: () -> Unit,
    onNavigateToWordList: () -> Unit,
    onNavigateToStats: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        HeaderSection(uiState)
        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Blue40.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Blue40
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "加载中...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Blue40
                    )
                }
            }
        } else {
            // Progress Ring Section
            ProgressSection(uiState)
            Spacer(modifier = Modifier.height(24.dp))

            // Task Cards
            TaskCards(
                uiState = uiState,
                onStudy = onNavigateToStudy,
                onReview = onNavigateToReview
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Stats Row
            StatsRow(uiState)
        }
    }
}

@Composable
private fun HeaderSection(uiState: HomeUiState) {
    val today = LocalDate.now()
    val dayOfWeek = today.format(DateTimeFormatter.ofPattern("EEEE", Locale.CHINESE))
    val dateStr = today.format(DateTimeFormatter.ofPattern("M月d日", Locale.CHINESE))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = getGreeting(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$dateStr $dayOfWeek",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Streak Badge
        Surface(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            color = YellowWarning.copy(alpha = 0.15f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = "连续打卡",
                    tint = RedError,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${uiState.streakDays}天",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = RedError
                )
            }
        }
    }
}

@Composable
private fun ProgressSection(uiState: HomeUiState) {
    val learnedProgress = if (uiState.totalWords > 0) {
        uiState.totalLearned.toFloat() / uiState.totalWords
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "学习进度",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularProgressWithText(
                    progress = learnedProgress,
                    label = "已学习",
                    value = "${uiState.totalLearned}",
                    color = Blue40
                )
                CircularProgressWithText(
                    progress = if (uiState.totalLearned > 0) {
                        uiState.totalMastered.toFloat() / uiState.totalLearned
                    } else 0f,
                    label = "已掌握",
                    value = "${uiState.totalMastered}",
                    color = GreenSuccess
                )
            }
        }
    }
}

@Composable
private fun TaskCards(
    uiState: HomeUiState,
    onStudy: () -> Unit,
    onReview: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Study Card
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = Blue40.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "今日新学",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${uiState.todayNewWords}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Blue40
                )
                Text(
                    text = "词",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onStudy,
                    colors = ButtonDefaults.buttonColors(containerColor = Blue40),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("开始学习")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Review Card
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = GreenSuccess.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "待复习",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${uiState.dueReviewCount}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = GreenSuccess
                )
                Text(
                    text = "词",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onReview,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("开始复习")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsRow(uiState: HomeUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        com.ielts.vocab.ui.components.StatCard(
            title = "总词量",
            value = "${uiState.totalWords}",
            modifier = Modifier.weight(1f),
            color = Blue40
        )
        com.ielts.vocab.ui.components.StatCard(
            title = "已学",
            value = "${uiState.totalLearned}",
            subtitle = "掌握 ${uiState.totalMastered}",
            modifier = Modifier.weight(1f),
            color = GreenSuccess
        )
        com.ielts.vocab.ui.components.StatCard(
            title = "待学",
            value = "${uiState.totalWords - uiState.totalLearned}",
            modifier = Modifier.weight(1f),
            color = YellowWarning
        )
    }
}

private fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 6 -> "夜深了"
        hour < 12 -> "早上好"
        hour < 14 -> "中午好"
        hour < 18 -> "下午好"
        else -> "晚上好"
    }
}
