package com.ielts.vocab.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ielts.vocab.algorithm.EbbinghausScheduler
import com.ielts.vocab.ui.components.MasteryButton
import com.ielts.vocab.ui.components.StudyProgressIndicator
import com.ielts.vocab.ui.components.WordCard
import com.ielts.vocab.ui.theme.GreenSuccess
import com.ielts.vocab.ui.theme.RedError
import com.ielts.vocab.ui.theme.YellowWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWordDetail: (Long) -> Unit,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("艾宾浩斯复习") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isFinished) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🎉", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "今日复习完成！",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    androidx.compose.material3.Button(onClick = onNavigateBack) {
                        Text("返回首页")
                    }
                }
            } else if (!uiState.isLoading && uiState.reviewWords.isNotEmpty()) {
                val (word, record) = uiState.reviewWords[uiState.currentIndex]

                StudyProgressIndicator(
                    current = uiState.currentIndex + 1,
                    total = uiState.reviewWords.size
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Stage info
                Text(
                    text = "复习阶段：${EbbinghausScheduler.getStageDescription(record.reviewStage)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                WordCard(
                    word = word.word,
                    phonetic = word.phonetic,
                    pos = word.pos,
                    definitions = word.definitions,
                    isFlipped = uiState.isFlipped,
                    onFlip = { viewModel.flipCard() },
                    onSpeak = { viewModel.speak() }
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MasteryButton(
                        text = "忘了",
                        color = RedError,
                        onClick = { viewModel.markWord(0) },
                        modifier = Modifier.weight(1f)
                    )
                    MasteryButton(
                        text = "模糊",
                        color = YellowWarning,
                        onClick = { viewModel.markWord(1) },
                        modifier = Modifier.weight(1f)
                    )
                    MasteryButton(
                        text = "记得",
                        color = GreenSuccess,
                        onClick = { viewModel.markWord(2) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
