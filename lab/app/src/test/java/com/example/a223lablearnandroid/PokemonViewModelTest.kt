package com.example.a223lablearnandroid

import com.example.a223lablearnandroid.utils.PokedexResponse
import com.example.a223lablearnandroid.utils.PokemonApiService
import com.example.a223lablearnandroid.utils.PokemonEntry
import com.example.a223lablearnandroid.utils.PokemonNetwork
import com.example.a223lablearnandroid.utils.PokemonSpecies
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockApi = mockk<PokemonApiService>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(PokemonNetwork)
        coEvery { PokemonNetwork.api } returns mockApi
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkObject(PokemonNetwork)
    }

    @Test
    fun fetchPokemon_updatesPokemonListOnSuccess() {
        // Arrange
        val mockEntries = listOf(
            PokemonEntry(1, PokemonSpecies("Bulbasaur", "url1")),
            PokemonEntry(2, PokemonSpecies("Ivysaur", "url2"))
        )
        val mockResponse = PokedexResponse(mockEntries)
        coEvery { mockApi.getKantoPokedex() } returns mockResponse

        // Act
        val viewModel = PokemonViewModel() // init calls fetchPokemon()

        // Assert
        assertEquals(mockEntries, viewModel.pokemonList.value)
    }

    @Test
    fun fetchPokemon_handlesError() {
        // Arrange
        coEvery { mockApi.getKantoPokedex() } throws Exception("Network Error")

        // Act
        val viewModel = PokemonViewModel()

        // Assert
        assertEquals(emptyList<PokemonEntry>(), viewModel.pokemonList.value)
    }
}