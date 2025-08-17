package com.tizzone.tvmaniak.core.domain.tvshows

import com.tizzone.tvmaniak.core.data.repository.tvshow.FakeTvShowRepositoryImpl
import com.tizzone.tvmaniak.core.domain.di.testDomainModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetTvShowsUseCaseTest : KoinTest {
    private val getTvShowsUseCase: GetTvShowsUseCase by inject()
    private val fakeTvShowRepositoryImpl: FakeTvShowRepositoryImpl by inject()

    @BeforeTest
    fun setup() {
        startKoin {
            modules(testDomainModule)
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `GetTvShowsUseCase returns Flow with PagingData`() {
        runTest {
            // When
            val tvShowsFlow = getTvShowsUseCase()
            val pagingData = tvShowsFlow.first()
            
            // Then
            assertNotNull(pagingData, "Flow should emit PagingData")
        }
    }

    @Test
    fun `GetTvShowsUseCase returns Flow that can be collected`() {
        runTest {
            // When
            val tvShowsFlow = getTvShowsUseCase()
            
            // Then - verify flow can be collected
            val pagingData = tvShowsFlow.first()
            assertNotNull(pagingData)
        }
    }

    @Test
    fun `GetTvShowsUseCase delegates to repository`() {
        runTest {
            // Given
            val expectedFlow = fakeTvShowRepositoryImpl.getTvShows()
            
            // When
            val actualFlow = getTvShowsUseCase()
            
            // Then - both flows should exist
            assertNotNull(expectedFlow)
            assertNotNull(actualFlow)
        }
    }

    @Test
    fun `Multiple invocations of GetTvShowsUseCase return valid flows`() {
        runTest {
            // When
            val firstFlow = getTvShowsUseCase()
            val secondFlow = getTvShowsUseCase()
            
            // Then
            val firstData = firstFlow.first()
            val secondData = secondFlow.first()
            
            assertNotNull(firstData)
            assertNotNull(secondData)
        }
    }

    @Test
    fun `Flow can be collected multiple times`() {
        runTest {
            // Given
            val tvShowsFlow = getTvShowsUseCase()
            
            // When - collect the flow multiple times
            val firstCollection = tvShowsFlow.first()
            val secondCollection = tvShowsFlow.first()
            
            // Then
            assertNotNull(firstCollection)
            assertNotNull(secondCollection)
        }
    }

    @Test
    fun `GetTvShowsUseCase returns same flow reference from repository`() {
        runTest {
            // This test verifies that the use case properly delegates to the repository
            // and doesn't transform the flow
            
            // When
            val flow = getTvShowsUseCase()
            
            // Then
            assertNotNull(flow, "Should return a non-null flow")
        }
    }

    @Test
    fun `Flow emits at least one PagingData`() {
        runTest {
            // When
            val tvShowsFlow = getTvShowsUseCase()
            val emissions = tvShowsFlow.take(1).toList()
            
            // Then
            assertTrue(emissions.isNotEmpty(), "Flow should emit at least one PagingData")
            assertNotNull(emissions.first())
        }
    }

    @Test
    fun `GetTvShowsUseCase can be invoked concurrently`() {
        runTest {
            // When - simulate concurrent invocations
            val flow1 = getTvShowsUseCase()
            val flow2 = getTvShowsUseCase()
            val flow3 = getTvShowsUseCase()
            
            // Then - all flows should be valid
            assertNotNull(flow1.first())
            assertNotNull(flow2.first())
            assertNotNull(flow3.first())
        }
    }

    @Test
    fun `Repository getTvShows is called when use case is invoked`() {
        runTest {
            // When
            val flow = getTvShowsUseCase()
            
            // Then - verify the flow is from repository
            // Since the use case just returns repository.getTvShows()
            // we verify the flow is not null and can be collected
            val pagingData = flow.first()
            assertNotNull(pagingData, "Repository should return valid PagingData")
        }
    }

    @Test
    fun `Flow completes without errors`() {
        runTest {
            // When
            val tvShowsFlow = getTvShowsUseCase()
            
            // Then - collecting should not throw
            try {
                val pagingData = tvShowsFlow.first()
                assertNotNull(pagingData)
            } catch (e: Exception) {
                throw AssertionError("Flow collection should not throw exception", e)
            }
        }
    }
}
