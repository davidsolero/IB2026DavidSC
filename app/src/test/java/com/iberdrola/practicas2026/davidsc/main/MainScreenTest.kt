package com.iberdrola.practicas2026.davidsc.main

import com.iberdrola.practicas2026.davidsc.domain.usecase.GetStreetsUseCase
import com.iberdrola.practicas2026.davidsc.ui.main.MainViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadStreets loads data successfully`() = runTest {
        val fakeUseCase = mockk<GetStreetsUseCase>()
        coEvery { fakeUseCase.invoke() } returns listOf("Calle A", "Calle B")

        val viewModel = MainViewModel(fakeUseCase, ioDispatcher = testDispatcher)

        viewModel.loadStreets()
        advanceUntilIdle()

        assertEquals(2, viewModel.streets.value.size)
        assertFalse(viewModel.isLoading.value)
        assertTrue(viewModel.error.value == null)
    }

    @Test
    fun `loadStreets sets error when fails`() = runTest {
        val fakeUseCase = mockk<GetStreetsUseCase>()
        coEvery { fakeUseCase.invoke() } throws RuntimeException("Error")

        val viewModel = MainViewModel(fakeUseCase, ioDispatcher = testDispatcher)

        viewModel.loadStreets()
        advanceUntilIdle()

        assertNotNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
        assertEquals(emptyList<String>(), viewModel.streets.value)
    }

    @Test
    fun `loadStreets sets loading correctly`() = runTest {
        val fakeUseCase = mockk<GetStreetsUseCase>()
        coEvery { fakeUseCase.invoke() } coAnswers {
            delay(10) // simula operación async
            emptyList()
        }

        val viewModel = MainViewModel(fakeUseCase, ioDispatcher = testDispatcher)

        val loadingStates = mutableListOf<Boolean>()

        val job = launch {
            viewModel.isLoading.drop(1).collect { loadingStates.add(it) }
        }

        viewModel.loadStreets()
        advanceUntilIdle()

        assertEquals(listOf(true, false), loadingStates)

        job.cancel()
    }
}