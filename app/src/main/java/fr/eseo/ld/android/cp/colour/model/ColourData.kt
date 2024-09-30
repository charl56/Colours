package fr.eseo.ld.android.cp.colour.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

data class ColourData(
    @StringRes
    val nameId : Int,
    @ColorRes
    val colourId : Int

){
}
