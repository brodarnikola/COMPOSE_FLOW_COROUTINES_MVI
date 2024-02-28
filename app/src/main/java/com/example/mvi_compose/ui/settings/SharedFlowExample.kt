package com.example.mvi_compose.ui.settings

import android.util.Log
import com.example.mvi_compose.movies.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SharedFlowExample
@Inject constructor(@IODispatcher private val ioDispatcher: CoroutineDispatcher) {

    // Backing property to avoid flow emissions from other classes
    private val _githubFlow = MutableSharedFlow<Unit>(replay = 0)
    val githubFlow = _githubFlow.asSharedFlow() // : SharedFlow<Event<String>> = _tickFlow

    init {
        var counter = 0
        CoroutineScope(ioDispatcher).launch {
            while (counter < 3) {
                counter++
                _githubFlow.emit(Unit)
                delay(5000)
                Log.d("Shared flow", "Shared flow trigerred counter: $counter")
            }
        }
    }
}