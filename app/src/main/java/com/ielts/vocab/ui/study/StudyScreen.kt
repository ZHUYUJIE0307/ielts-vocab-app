package com.ielts.vocab.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.ielts.vocab.ui.components.MasteryButton
import com.ielts.vocab.ui.components.StudyProgressIndicator
import com.ielts.vocab.ui.components.WordCard
import com.ielts.vocab.ui.theme.GreenSuccess
import com.ielts.vocab.ui.theme.RedError
import com.ielts.vocab.ui.theme.YellowWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWordDetail: (Long) -> Unit,
    viewModel: StudyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("今日学习") },
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
                FinishedView(onNavigateBack = onNavigateBack)
            } else if (!uiState.isLoading && uiState.words.isNotEmpty()) {
                // Progress
                StudyProgressIndicator(
                    current = uiState.currentIndex + 1,
                    total = uiState.words.size
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Word Card
                val currentWord = uiState.words[uiState.currentIndex]
                WordCard(
                    word = currentWord.word,
                    phonetic = currentWord.phonetic,
                    pos = currentWord.pos,
                    definitions = currentWord.definitions,
                    isFlipped = uiState.isFlipped,
                    onFlip = { viewModel.flipCard() },
                    onSpeak = { viewModel.speak() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Association card (if available)
                uiState.association?.let { assoc ->
                    if (assoc.roots.isNotEmpty() || assoc.association.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                if (assoc.roots.isNotEmpty()) {
                                    Text(
                                        text = "词根词缀",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = assoc.roots,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                if (assoc.association.isNotEmpty()) {
                                    Text(
                                        text = "联想记忆",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = assoc.association,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MasteryButton(
                        text = "不认识",
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
                        text = "认识",
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

@Composable
private fun FinishedView(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "今日学习完成！",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "继续保持，每天进步一点点",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        androidx.compose.material3.Button(
            onClick = onNavigateBack,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("返回首页")
        }
    }
}
