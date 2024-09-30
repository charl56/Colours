package fr.eseo.ld.android.cp.colour.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fr.eseo.ld.android.cp.colour.R
import fr.eseo.ld.android.cp.colour.model.ColourData
import fr.eseo.ld.android.cp.colour.model.Colours
import fr.eseo.ld.android.cp.colour.model.PreviousGuess
import fr.eseo.ld.android.cp.colour.ui.viewmodels.GameViewModel
import kotlin.math.max


@Composable
fun PreviousGuessCard(previousGuess : PreviousGuess, modifier: Modifier) {

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = if(previousGuess.result)
                    R.drawable.yes
                else
                    R.drawable.no
                ),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = modifier.height(IntrinsicSize.Min).padding(16.dp)
            )
            Column(
                modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(text= stringResource(id = R.string.actual_message, stringResource(previousGuess.correctColourData.nameId)))
                Text(text= stringResource(id = R.string.guessed_message, stringResource(previousGuess.guessedColourData.nameId)))
            }
        }
    }
}


@Composable
fun PreviousGuessList(previousGuesses : List<PreviousGuess>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()

    LaunchedEffect(previousGuesses.size) {
        listState.animateScrollToItem(index = max(previousGuesses.size -1 , 0))
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(previousGuesses){
                previousGuess -> PreviousGuessCard(
            previousGuess = previousGuess, modifier = modifier.padding(8.dp)
        )
        }
    }
}


@Composable
fun BoxWithText(textId : Int, colourId: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                colorResource(id = R.color.colour_grey)
            )
            .padding(vertical = 16.dp)
            .fillMaxWidth(0.75f),
        contentAlignment = Alignment.Center
    )
    {
        Text(
            text = stringResource(id = textId),
            color = colorResource(id = colourId),
            fontSize = 58.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GameCard(
    currentCorrectColour: ColourData,
     currentIncorrectColour: ColourData,
     option1: ColourData,
     option2: ColourData,
     onClick: (Int) -> Unit,
     modifier: Modifier = Modifier
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ){
        BoxWithText(
            textId = currentIncorrectColour.nameId,
            colourId = currentCorrectColour.colourId
        )
        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {onClick(option1.nameId)}
        ) {
            Text(
                text = stringResource(id = option1.nameId),
                fontSize = 36.sp,
            )
        }
        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = {onClick(option2.nameId)}
        ) {
            Text(
                text = stringResource(id = option2.nameId),
                fontSize = 36.sp,
            )
        }
    }
}


@Composable
fun GameScreen(
    viewModel: GameViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val gameUiState by viewModel.uiState.collectAsState()

    val (option1,option2) = remember(
        gameUiState.currentCorrectColour,
        gameUiState.currentIncorrectColour
    ){
        if((0..1).random()==0){
            gameUiState.currentCorrectColour to gameUiState.currentIncorrectColour
        }else{
            gameUiState.currentIncorrectColour to gameUiState.currentCorrectColour
        }
    }

    DisposableEffect(Unit) {
        viewModel.startGame()
        onDispose {
            viewModel.stopGame()
        }
    }

    if(gameUiState.timeLeft <= 0){
        LaunchedEffect(Unit) {
            Toast.makeText(context, R.string.game_over, Toast.LENGTH_LONG).show()
            viewModel.recordScore()
            navController.popBackStack()
        }
    }


    Column (modifier = modifier
        .statusBarsPadding()
        .safeDrawingPadding()
        .padding(8.dp)
    ){
        Text(
            text = stringResource (id=R.string.time_left_label, gameUiState.timeLeft/1000, (gameUiState.timeLeft%1000)/10)
        )
        GameCard(
            currentCorrectColour = gameUiState.currentCorrectColour,
            currentIncorrectColour = gameUiState.currentIncorrectColour,
            option1 = option1,
            option2 = option2,
            onClick = {viewModel.checkUserGuess(it)},
            modifier = modifier.padding(8.dp)
        )
        Box(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ){
            PreviousGuessList(
                previousGuesses = gameUiState.previousGuesses)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GameScreenPreview(){
//    GameScreen(
//        viewModel = GameViewModel(),
//        navController = NavController()
//    )
}


