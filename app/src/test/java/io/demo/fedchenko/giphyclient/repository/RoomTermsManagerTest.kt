package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.room.DbTerm
import io.demo.fedchenko.giphyclient.room.TermDao
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito
import org.mockito.internal.verification.VerificationModeFactory.times

class RoomTermsManagerTest {

    @Test
    fun getTermsNotEmpty() {
        val termDao = Mockito.mock(TermDao::class.java)
        val publisher = ConflatedBroadcastChannel(emptyList<DbTerm>())

        Mockito.`when`(termDao.getTermsFlow()).thenReturn(publisher.asFlow())

        val roomTermsManager = RoomTermsManager(termDao)
        val result = roomTermsManager.getTerms()

        runBlocking {
            publisher.offer(listOf(DbTerm(12, "term1"), DbTerm(14, "term2")))
            result.take(1).collect { assert(it == listOf("term1", "term2")) }

            publisher.offer(listOf(DbTerm(17, "term3")))
            result.take(1).collect { assert(it == listOf("term3")) }
        }
    }

    @Test
    fun getTermsEmpty() {
        val termDao = Mockito.mock(TermDao::class.java)
        val publisher = ConflatedBroadcastChannel(emptyList<DbTerm>())

        Mockito.`when`(termDao.getTermsFlow()).thenReturn(publisher.asFlow())

        val roomTermsManager = RoomTermsManager(termDao)
        val result = roomTermsManager.getTerms()

        publisher.offer(listOf())
        runBlocking {
            result.take(1).collect { assert(it == emptyList<String>()) }
        }
    }

    @Test
    fun saveNotEmptyTermTest() {
        val termDao = Mockito.mock(TermDao::class.java)
        val roomTermsManager = RoomTermsManager(termDao)
        runBlocking {
            roomTermsManager.saveTerm("term1")
            Mockito.verify(termDao, times(1)).addTerm(DbTerm("term1".hashCode().toLong(), "term1"))
        }
    }

    @Test
    fun saveEmptyTermTest() {
        val termDao = Mockito.mock(TermDao::class.java)
        val roomTermsManager = RoomTermsManager(termDao)
        runBlocking {
            roomTermsManager.saveTerm("")
            Mockito.verify(termDao, times(1)).addTerm(DbTerm("".hashCode().toLong(), ""))
        }
    }
}