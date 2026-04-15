package com.ielts.vocab.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ielts.vocab.data.local.entity.AssociationEntity
import com.ielts.vocab.data.local.entity.WordEntity
import com.ielts.vocab.data.repository.LearningRepository
import com.ielts.vocab.data.repository.WordRepository
import com.ielts.vocab.service.TtsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StudyUiState(
    val words: List<WordEntity> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val association: AssociationEntity? = null,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false,
    val knownLevel: Int = -1 // 0=不认识, 1=模糊, 2=认识
)

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val learningRepository: LearningRepository,
    private val ttsService: TtsService
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudyUiState())
    val uiState: StateFlow<StudyUiState> = _uiState.asStateFlow()

    private val dailyGoal = 20

    init {
        loadNewWords()
    }

    private fun loadNewWords() {
        viewModelScope.launch {
            val unlearned = wordRepository.getUnlearnedWords(dailyGoal, 0)
            _uiState.value = _uiState.value.copy(
                words = unlearned,
                isLoading = false,
                isFinished = unlearned.isEmpty()
            )
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
        if (nextIndex >= state.words.size) {
            _uiState.value = state.copy(isFinished = true)
        } else {
            _uiState.value = state.copy(
                currentIndex = nextIndex,
                isFlipped = false,
                association = null,
                knownLevel = -1
            )
            loadAssociation()
        }
    }

    private fun loadAssociation() {
        val word = getCurrentWord() ?: return
        viewModelScope.launch {
            val assoc = wordRepository.getAssociationSync(word.id)
            _uiState.value = _uiState.value.copy(association = assoc)
        }
    }

    private fun getCurrentWord(): WordEntity? {
        val state = _uiState.value
        return if (state.currentIndex < state.words.size) state.words[state.currentIndex] else null
    }

    fun getCurrentWordId(): Long = getCurrentWord()?.id ?: 0L
}
