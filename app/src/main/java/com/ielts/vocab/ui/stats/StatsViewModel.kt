package com.ielts.vocab.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ielts.vocab.data.local.entity.DailyStatEntity
import com.ielts.vocab.data.repository.LearningRepository
import com.ielts.vocab.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
    val totalWords: Int = 0,
    val totalLearned: Int = 0,
    val totalMastered: Int = 0,
    val totalUnlearned: Int = 0,
    val streakDays: Int = 0,
    val recentStats: List<DailyStatEntity> = emptyList()
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            combine(
                wordRepository.getTotalCount(),
                learningRepository.getLearnedCount(),
                learningRepository.getMasteredCount()
            ) { total, learned, mastered ->
                _uiState.value.copy(
                    totalWords = total,
                    totalLearned = learned,
                    totalMastered = mastered,
                    totalUnlearned = total - learned
                )
            }.collect { _uiState.value = it }
        }

        viewModelScope.launch {
            val streak = learningRepository.getStreakCount()
            _uiState.value = _uiState.value.copy(streakDays = streak)
        }

        viewModelScope.launch {
            learningRepository.getRecentStats(30).collect { stats ->
                _uiState.value = _uiState.value.copy(recentStats = stats)
            }
        }
    }
}
