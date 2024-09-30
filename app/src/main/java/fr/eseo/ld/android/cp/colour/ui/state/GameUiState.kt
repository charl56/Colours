package fr.eseo.ld.android.cp.colour.ui.state

import fr.eseo.ld.android.cp.colour.model.ColourData
import fr.eseo.ld.android.cp.colour.model.Colours
import fr.eseo.ld.android.cp.colour.model.PreviousGuess

data class GameUiState(
    val currentCorrectColour : ColourData = Colours.colours[0],
    val currentIncorrectColour : ColourData = Colours.colours[1],
    val currentScore : Int = 0,
    val guessedColourIndex : Int = -1,
    val timeLeft : Long = 60000,
    val lastScore : Int = 0,
    val localHighScore : Int = 0,
    val previousGuesses : List<PreviousGuess> = emptyList()
)
