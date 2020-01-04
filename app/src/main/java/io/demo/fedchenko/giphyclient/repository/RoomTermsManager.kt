package io.demo.fedchenko.giphyclient.repository

import io.demo.fedchenko.giphyclient.room.AppDataBase
import io.demo.fedchenko.giphyclient.room.DbTerm
import io.demo.fedchenko.giphyclient.room.TermDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTermsManager(private val termDao: TermDao) : TermsManager {

    override fun getTerms(): Flow<List<String>> =
        termDao.getTermsFlow().map { it.mapNotNull { dbTerm -> dbTerm.term } }

    override suspend fun saveTerm(term: String) =
        termDao.addTerm(DbTerm(term.hashCode().toLong(), term))
}