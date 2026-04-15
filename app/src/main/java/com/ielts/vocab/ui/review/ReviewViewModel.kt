package com.ielts.vocab.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ielts.vocab.data.local.entity.LearningRecordEntity
import com.ielts.vocab.data.local.entity.WordEntity
import com.ielts.vocab.data.repository.LearningRepository
import com.ielts.vocab.data.repository.WordRepository
import com.ielts.vocab.service.TtsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ReviewUiState(
    val reviewWords: List<Pair<WordEntity, LearningRecordEntity>> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val learningRepository: LearningRepository,
    private val ttsService: TtsService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init {
        loadDueReviews()
    }

    private fun loadDueReviews() {
        val today = LocalDate.now().toString()
        viewModelScope.launch {
            learningRepository.getDueReviews(today).collect { records ->
                val pairs = mutableListOf<Pair<WordEntity, LearningRecordEntity>>()
                for (record in records) {
                    val word = wordRepository.getWordByIdSync(record.wordId)
                    if (word != null) {
                        pairs.add(word to record)
                    }
                }
                _uiState.value = _uiState.value.copy(
                    reviewWords = pairs,
                    isLoading = false,
                    isFinished = pairs.isEmpty()
                )
            }
        }
    }

    fun flipCard() {
        _uiState.value = _uiState.value.copy(isFlipped = !_uiState.value.isFlipped)
    }

    fun speak() {
        val word = getCurrentWord() ?: return
        ttsService.speak(word.word)
    }

    fun markWord(knownLevel: Int) {
        val word = getCurrentWord() ?: return
        viewModelScope.launch {
            learningRepository.markWordLearned(word.id, knownLevel)
            nextWord()
        }
    }

    private fun nextWord() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.reviewWords.size) {
            _uiState.value = state.copy(isFinished = true)
        } else {
            _uiState.value = state.copy(
                currentIndex = nextIndex,
                isFlipped = false
            )
        }
    }

    private fun getCurrentWord(): WordEntity? {
        val state = _uiState.value
        return if (state.currentIndex < state.reviewWords.size) {
            state.reviewWords[state.currentIndex].first
        } else null
    }
}
