package com.ielts.vocab.ui.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ielts.vocab.data.local.entity.AssociationEntity
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
import javax.inject.Inject

data class WordDetailUiState(
    val word: WordEntity? = null,
    val association: AssociationEntity? = null,
    val learningRecord: LearningRecordEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val learningRepository: LearningRepository,
    private val ttsService: TtsService
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordDetailUiState())
    val uiState: StateFlow<WordDetailUiState> = _uiState.asStateFlow()

    fun loadWord(wordId: Long) {
        viewModelScope.launch {
            val word = wordRepository.getWordByIdSync(wordId)
            val assoc = wordRepository.getAssociationSync(wordId)
            val record = learningRepository.getRecordByWordIdSync(wordId)
            _uiState.value = WordDetailUiState(
                word = word,
                association = assoc,
                learningRecord = record,
                isLoading = false
            )
        }
    }

    fun speak() {
        _uiState.value.word?.let { ttsService.speak(it.word) }
    }
}
