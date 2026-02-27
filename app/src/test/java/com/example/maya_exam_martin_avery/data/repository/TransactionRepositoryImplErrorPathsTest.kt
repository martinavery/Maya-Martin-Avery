package com.example.maya_exam_martin_avery.data.repository

import com.example.maya_exam_martin_avery.data.local.dao.TransactionDao
import com.example.maya_exam_martin_avery.data.remote.TransactionsApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TransactionRepositoryImplErrorPathsTest {
    private lateinit var server: MockWebServer
    private lateinit var api: TransactionsApi
    private lateinit var dao: TransactionDao
    private lateinit var repo: TransactionRepositoryImpl

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        api =
            Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TransactionsApi::class.java)

        dao = mockk()
        // Repository constructor doesn't call observe, but keep it safe for any future changes.
        every { dao.observeByUserId(any()) } returns flowOf(emptyList())

        repo = TransactionRepositoryImpl(transactionDao = dao, transactionsApi = api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getRemoteSampleTransactions returns failure on non-2xx responses`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("""{"error":"boom"}"""))

        val result = repo.getRemoteSampleTransactions()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `getRemoteSampleTransactions returns failure on invalid json`() = runTest {
        // Retrofit/Gson should throw when JSON cannot be parsed into a list.
        server.enqueue(MockResponse().setResponseCode(200).setBody("not-json"))

        val result = repo.getRemoteSampleTransactions()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `postRemoteSampleTransaction returns failure on non-2xx responses`() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("""{"error":"boom"}"""))

        val result = repo.postRemoteSampleTransaction(userId = 1L, amount = 1.0, description = "Sent ₱1.00")

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `saveLocalSentTransaction returns failure when dao insert throws`() = runTest {
        val expected = IllegalStateException("db exploded")
        val localDao: TransactionDao = mockk()
        val unusedApi: TransactionsApi = mockk()

        coEvery { localDao.insert(any()) } throws expected
        every { localDao.observeByUserId(any()) } returns flowOf(emptyList())

        val localRepo = TransactionRepositoryImpl(transactionDao = localDao, transactionsApi = unusedApi)

        val result = localRepo.saveLocalSentTransaction(
            userId = 1L,
            amount = 1.0,
            description = "Sent ₱1.00",
            createdAtEpochMs = 1_700_000_000_000L,
        )

        assertTrue(result.isFailure)
        assertSame(expected, result.exceptionOrNull())
        coVerify(exactly = 1) { localDao.insert(any()) }
    }
}

