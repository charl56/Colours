package fr.eseo.ld.android.cp.colour.model

data class PreviousGuess(
    val correctColourData: ColourData,
    val guessedColourData: ColourData
){
    val result : Boolean

    init {
        result = correctColourData == guessedColourData
    }


}
