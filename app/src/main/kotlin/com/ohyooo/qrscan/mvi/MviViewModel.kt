package com.ohyooo.qrscan.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface MviIntent

interface MviState

interface MviEffect

abstract class MviViewModel<Intent : MviIntent, State : MviState, Effect : MviEffect>(
    initialState: State
) : ViewModel() {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<Effect>(extraBufferCapacity = 64)
    val effects: SharedFlow<Effect> = _effects.asSharedFlow()

    protected val currentState: State
        get() = _state.value

    fun dispatch(intent: Intent) {
        reduce(intent)
    }

    protected abstract fun reduce(intent: Intent)

    protected fun setState(reducer: State.() -> State) {
        _state.update { state -> state.reducer() }
    }

    protected fun sendEffect(effect: Effect) {
        if (!_effects.tryEmit(effect)) {
            viewModelScope.launch {
                _effects.emit(effect)
            }
        }
    }
}
