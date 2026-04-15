package com.ielts.vocab.ui.wordlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ielts.vocab.data.local.entity.WordEntity
import com.ielts.vocab.data.repository.LearningRepository
import com.ielts.vocab.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordListUiState(
    val wordListIndices: List<Int> = emptyList(),
    val currentListIndex: Int = 1,
    val words: List<WordEntity> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<WordEntity> = emptyList(),
    val learnedCount: Int = 0,
    val totalInList: Int = 0
)

@HiltViewModel
class WordListViewModel @Inject constructor(
    private val wordRepository: WordRepository,
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordListUiState())
    val uiState: StateFlow<WordListUiState> = _uiState.asStateFlow()

    init {
        loadWordListIndices()
        selectWordList(1)
    }

    private fun loadWordListIndices() {
        viewModelScope.launch {
            wordRepository.getWordListIndices().collect { indices ->
                _uiState.value = _uiState.value.copy(wordListIndices = indices)
            }
        }
    }

    fun selectWordList(index: Int) {
        _uiState.value = _uiState.value.copy(currentListIndex = index)
        viewModelScope.launch {
            wordRepository.getByWordList(index).collect { words ->
                _uiState.value = _uiState.value.copy(
                    words = words,
                    totalInList = words.size
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
        } else {
            viewModelScope.launch {
                wordRepository.searchWords(query).collect { results ->
                    _uiState.value = _uiState.value.copy(searchResults = results)
                }
            }
        }
    }
}
