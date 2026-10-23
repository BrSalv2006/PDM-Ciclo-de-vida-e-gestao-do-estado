package pt.ubi.di.pdm.lifestatelab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // Guarda o texto do monitor na RAM para sobreviver à rotação
    private val _logsState = MutableStateFlow("")
    val logsState: StateFlow<String> = _logsState.asStateFlow()

    fun addLog(source: String, callback: String) {
        val entry = "• [$source] $callback\n"
        _logsState.value += entry
    }

    fun clearLogs() {
        _logsState.value = ""
    }

    // Variável tradicional em memória RAM (sobrevive a rotação, perde-se em Process Death)
    var vmCounter: Int = 0

    // SavedStateHandle: sobrevive a rotação e a Morte de Processo (Process Death)
    val savedText: StateFlow<String> = savedStateHandle.getStateFlow("USER_NOTE", "")

    fun updateSavedText(note: String) {
        savedStateHandle["USER_NOTE"] = note
    }

    // StateFlow (Reatividade Kotlin)
    private val _flowCounter = MutableStateFlow(0)
    val flowCounter: StateFlow<Int> = _flowCounter.asStateFlow()

    fun incrementFlow() {
        _flowCounter.value += 1
    }

    // LiveData (Reatividade Java/Android)
    private val _liveDataCounter = MutableLiveData(0)
    val liveDataCounter: LiveData<Int> = _liveDataCounter

    fun incrementLiveData() {
        val current = _liveDataCounter.value ?: 0
        _liveDataCounter.value = current + 1
    }
}