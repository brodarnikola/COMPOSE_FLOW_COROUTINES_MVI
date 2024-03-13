package com.example.mvi_compose.ui.rxJavaExamples

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.mvi_compose.movies.di.github.GithubNetwork
import com.example.mvi_compose.movies.network.data.github.GithubResponseApi
import com.example.mvi_compose.movies.repositories.GithubRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import javax.inject.Inject


@HiltViewModel
class RxJava2ViewModel @Inject constructor(
    @GithubNetwork private val repository: GithubRepo
) : ViewModel() {

    // this is a single example of rxjava2 for github repositories
    fun getGithubRepositories(query: String, page: Int, perPage: Int) : Single<GithubResponseApi> {
        return repository.getSearchRepositorieRxJava2(query, page, perPage)
    }

    private val _incrementNumberAutomaticallyByOne = MediatorLiveData<Int>().apply {
        value = 0
    }

    val incrementNumberAutomaticallyByOne: LiveData<Int> = _incrementNumberAutomaticallyByOne

    fun incrementAutomaticallyByOne() {
        Log.d(
            "TestVM",
            "The automatic amount is being increment, current value = ${_incrementNumberAutomaticallyByOne.value}"
        )
        _incrementNumberAutomaticallyByOne.value?.let { number ->
            _incrementNumberAutomaticallyByOne.value = number + 1
        }
    }

}