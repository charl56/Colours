package fr.eseo.ld.android.cp.colour.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.eseo.ld.android.cp.colour.model.ColourData
import fr.eseo.ld.android.cp.colour.model.Colours
import fr.eseo.ld.android.cp.colour.model.PreviousGuess
import fr.eseo.ld.android.cp.colour.model.WhatColourDataStore
import fr.eseo.ld.android.cp.colour.ui.state.GameUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random


class GameViewModel(private val dataStore: WhatColourDataStore): ViewModel() {


    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()
    var userGuess by mutableStateOf(-1)
        private set
    private var timerJob : Job? = null

    init{
        viewModelScope.launch{
            dataStore.uiStateFlow.collect{
                    state -> _uiState.value = state
            }
        }
    }


    private fun selectRandomColour(exemptColour : ColourData? = null) : ColourData {
        val filteredList = if(exemptColour != null){
            Colours.colours.filter{it !=exemptColour}
        }
        else{
            Colours.colours
        }

        val randomIndex = Random.nextInt(filteredList.size)
        return filteredList[randomIndex]
    }


    private fun updateGameState(score : Int){
        val correctColour = selectRandomColour()
        val incorrectColour = selectRandomColour(correctColour)
        _uiState.update{
        currentState ->
            currentState.copy(
                currentCorrectColour = correctColour,
                currentIncorrectColour = incorrectColour,
                currentScore = score
            )
        }
    }


    private fun updateGameState(time : Long){
        _uiState.update{
        currentState ->
            currentState.copy(
                timeLeft = time
            )
        }
    }


    private fun updateGameState(lastScore : Int, highScore : Int){
        viewModelScope.launch {
            dataStore.saveScores(lastScore, highScore)
            _uiState.value =
                _uiState.value.copy(localHighScore = highScore, lastScore = lastScore)
        }
    }


    private fun updateGameState(previousGuess : PreviousGuess){
        _uiState.update{
                currentState ->
            currentState.copy(
                previousGuesses = _uiState.value.previousGuesses + previousGuess
            )
        }
    }


    private fun startTimer(){
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while(_uiState.value.timeLeft>0){
                val delayTime : Long = if(_uiState.value.timeLeft > 10000){
                    1000
                }else{
                    100
                }
                delay(delayTime)
                val timeLeftAfterTick = _uiState.value.timeLeft.minus(delayTime)
                updateGameState(timeLeftAfterTick)
            }
        }
    }


    fun stopGame(){
        timerJob?.cancel()
    }


    fun startGame(){
        val correctColour = selectRandomColour()
        val incorrectColour = selectRandomColour(correctColour)
        _uiState.update{
        currentState ->
            currentState.copy(
                currentCorrectColour = correctColour,
                currentIncorrectColour = incorrectColour,
                currentScore = 0,
                timeLeft = 60000,
                guessedColourIndex = -1,
                previousGuesses = emptyList()
            )
        }
        startTimer()
    }


    fun checkUserGuess(guessedColourId: Int) {
        userGuess = guessedColourId
        val previousGuess: PreviousGuess
        if (userGuess.equals(_uiState.value.currentCorrectColour.nameId)) {
            previousGuess = PreviousGuess(
                _uiState.value.currentCorrectColour, _uiState.value.currentCorrectColour
            )
            val updatedScore = _uiState.value.currentScore.plus(1)
            updateGameState(score = updatedScore)
        } else {
            previousGuess = PreviousGuess(
                _uiState.value.currentCorrectColour, _uiState.value.currentIncorrectColour
            )
            val timeLeftAfterPenalty = _uiState.value.timeLeft.minus(2000)
            updateGameState(time = timeLeftAfterPenalty)
        }
        updateGameState(previousGuess)
        userGuess = -1
    }


    fun recordScore(){
        val lastScore = _uiState.value.currentScore
        val highScore =
            if(_uiState.value.currentScore > _uiState.value.localHighScore)
                _uiState.value.currentScore
            else
                _uiState.value.localHighScore
        updateGameState(lastScore, highScore)

    }


    class GameViewModelFactory(
        private val dataStore: WhatColourDataStore
    ) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(GameViewModel::class.java)){
                return GameViewModel(dataStore) as T
            }
            throw IllegalArgumentException("Wrong VM class")
        }
    }

}
