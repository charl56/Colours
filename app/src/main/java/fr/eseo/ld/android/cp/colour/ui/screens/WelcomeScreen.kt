package fr.eseo.ld.android.cp.colour.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.eseo.ld.android.cp.colour.R
import fr.eseo.ld.android.cp.colour.ui.state.GameUiState
import fr.eseo.ld.android.cp.colour.ui.viewmodels.GameViewModel

@Composable
fun WelcomeTitle() {
    Text(
        text = stringResource(id = R.string.game_description),
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 32.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun WelcomeButton(
    text : String,
    onClick : ()-> Unit,
    enabled : Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.padding(8.dp).fillMaxWidth()
    )
    {
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }

}


@Composable
fun WelcomeScores(gameUiState: GameUiState) {
    Text(
        text = stringResource(R.string.last_score, gameUiState.lastScore),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(8.dp)
    )
    Text(
        text = stringResource(R.string.local_high_score, gameUiState.localHighScore),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(8.dp)
    )
}

@Composable
fun WelcomeScreen(
        viewModel : GameViewModel,
        playGameClick : () ->Unit,
    ){
    Column (
        verticalArrangement = Arrangement.SpaceBetween
    ){
        val gameUiState by viewModel.uiState.collectAsState()

        WelcomeTitle()
        WelcomeButton(text = stringResource(R.string.play_game_button), onClick = {playGameClick()})
        WelcomeScores(gameUiState)
        WelcomeButton(text = stringResource(R.string.show_global_scores_button), onClick = {})
    }
}
