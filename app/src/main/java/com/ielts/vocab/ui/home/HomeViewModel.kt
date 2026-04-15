package com.ielts.vocab.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ielts.vocab.data.repository.LearningRepository
import com.ielts.vocab.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val todayNewWords: Int = 0,
    val dueReviewCount: Int = 0,
    val totalLearned: Int = 0,
    val totalMastered: Int = 0,
    val totalWords: Int = 0,
    val streakDays: Int = 0,
    val dailyGoal: Int = 20,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        val today = LocalDate.now().toString()
        viewModelScope.launch {
            combine(
                wordRepository.getTotalCount(),
                learningRepository.getLearnedCount(),
                learningRepository.getMasteredCount(),
                learningRepository.getDueReviewCount(today)
            ) { total, learned, mastered, dueReview ->
                HomeUiState(
                    totalWords = total,
                    totalLearned = learned,
                    totalMastered = mastered,
                    dueReviewCount = dueReview,
                    isLoading = false
                )
            }.collect { state -> _uiState.value = state }
        }

        viewModelScope.launch {
            val streak = learningRepository.getStreakCount()
            _uiState.value = _uiState.value.copy(streakDays = streak)
        }
    }
}
