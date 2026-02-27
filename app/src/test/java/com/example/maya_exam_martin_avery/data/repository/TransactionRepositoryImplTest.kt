package com.example.maya_exam_martin_avery.data.repository

import com.example.maya_exam_martin_avery.data.local.dao.TransactionDao
import com.example.maya_exam_martin_avery.data.local.entities.TransactionEntity
import com.example.maya_exam_martin_avery.data.remote.TransactionsApi
import com.example.maya_exam_martin_avery.domain.model.TransactionDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TransactionRepositoryImplTest {

    private lateinit var server: MockWebServer
    private lateinit var api: TransactionsApi
    private lateinit var fakeDao: FakeTransactionDao
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

        fakeDao = FakeTransactionDao()
        repo = TransactionRepositoryImpl(transactionDao = fakeDao, transactionsApi = api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `getRemoteSampleTransactions maps posts to RemoteSample`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    [
                      {"id": 1, "userId": 10, "title": "t1", "body": "b1"},
                      {"id": 2, "userId": 11, "title": "t2", "body": "b2"}
                    ]
                    """.trimIndent(),
                ),
        )

        val result = repo.getRemoteSampleTransactions()

        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(2, list.size)
        assertEquals(TransactionDomain.RemoteSample(id = "remote-1", userId = 10, title = "t1", body = "b1"), list[0])
    }

    @Test
    fun `postRemoteSampleTransaction hits posts endpoint`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody("""{"id": 101, "userId": 1, "title": "Sent ₱44.00", "body": "Sent ₱44.00"}"""),
        )

        val result =
            repo.postRemoteSampleTransaction(
                userId = 1,
                amount = 44.0,
                description = "Sent ₱44.00",
            )

        assertTrue(result.isSuccess)
        val request = server.takeRequest()
        assertEquals("/posts", request.path)
        assertEquals("POST", request.method)
    }

    @Test
    fun `saveLocalSentTransaction is observable via Flow`() = runBlocking {
        val userId = 123L
        val now = 1_700_000_000_000L

        val before = repo.observeLocalTransactions(userId).first()
        assertTrue(before.isEmpty())

        repo.saveLocalSentTransaction(
            userId = userId,
            amount = 12.34,
            description = "Sent ₱12.34",
            createdAtEpochMs = now,
        ).getOrThrow()

        val after = repo.observeLocalTransactions(userId).first()
        assertEquals(1, after.size)
        assertEquals(userId, after[0].userId)
        assertEquals(12.34, after[0].amount, 0.0)
    }

    private class FakeTransactionDao : TransactionDao {
        private var nextId = 1L
        private val data = mutableListOf<TransactionEntity>()
        private val flow = MutableStateFlow<List<TransactionEntity>>(emptyList())

        override suspend fun insert(transaction: TransactionEntity): Long {
            val id = nextId++
            data += transaction.copy(id = id)
            flow.value = data.toList()
            return id
        }

        override fun observeByUserId(userId: Long): Flow<List<TransactionEntity>> {
            return flow.map { list -> list.filter { it.userId == userId } }
        }
    }
}

