package com.pokedex.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mxalbert.sharedelements.FadeMode
import com.mxalbert.sharedelements.MaterialArcMotionFactory
import com.mxalbert.sharedelements.MaterialContainerTransformSpec
import com.mxalbert.sharedelements.ProgressThresholds
import com.mxalbert.sharedelements.SharedElementsRoot
import com.mxalbert.sharedelements.SharedElementsRootScope
import com.mxalbert.sharedelements.SharedElementsTransitionSpec
import com.pokedex.domain.model.Pokemon
import com.pokedex.presentation.pokemonDetails.PokemonDetailsScreen
import com.pokedex.presentation.pokemonList.PokemonListScreen
import com.pokedex.presentation.pokemonList.PokemonViewModel
import com.ramcosta.composedestinations.annotation.Destination

var selectedPokemon: Int by mutableIntStateOf(-1)
var previousSelectedPokemon: Int = -1

const val ListScreen = "list"
const val DetailsScreen = "details"

const val TransitionDurationMillis = 400

val CrossFadeTransitionSpec = SharedElementsTransitionSpec(
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.Cross,
    fadeProgressThresholds = ProgressThresholds(0.10f, 0.40f)
)
val MaterialFadeInTransitionSpec = MaterialContainerTransformSpec(
    pathMotionFactory = MaterialArcMotionFactory,
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.In
)
val MaterialFadeOutTransitionSpec = MaterialContainerTransformSpec(
    pathMotionFactory = MaterialArcMotionFactory,
    durationMillis = TransitionDurationMillis,
    fadeMode = FadeMode.Out
)

@Destination(start = true)
@Composable
fun PokemonListRoot(
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true
        )
        systemUiController.setNavigationBarColor(color = Color.Transparent)
    }

    SharedElementsRoot {
        BackHandler(enabled = selectedPokemon >= 0) {
            changePokemon(-1, state.pokemonList)
        }

        val listState = rememberLazyGridState()
        Crossfade(
            targetState = selectedPokemon,
            animationSpec = tween(durationMillis = TransitionDurationMillis),
            label = "crossFade anim"
        ) { item ->
            when {
                item < 0 -> PokemonListScreen(listState)
                else -> PokemonDetailsScreen(state.pokemonList[item])
            }
        }
    }
}

fun SharedElementsRootScope.changePokemon(
    user: Int,
    pokemonList: List<Pokemon>
) {
    val currentPokemon = selectedPokemon
    if (currentPokemon != user) {
        val targetPokemon = if (user >= 0) user else currentPokemon
        if (targetPokemon >= 0) {
            pokemonList[targetPokemon].let {
                prepareTransition(it.name, it.imageUrl)
            }
        }
        previousSelectedPokemon = selectedPokemon
        selectedPokemon = user
    }
}