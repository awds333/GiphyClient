package io.demo.fedchenko.giphyclient.repository.loader

import io.demo.fedchenko.giphyclient.repository.TermsManager
import io.demo.fedchenko.giphyclient.room.AppDataBase
import io.demo.fedchenko.giphyclient.room.DbTerm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTermsManager(appDataBase: AppDataBase) : TermsManager {
    private val termDao = appDataBase.termDao()

    override fun getTerms(): Flow<List<String>> =
        termDao.getTermsFlow().map { it.mapNotNull { dbTerm -> dbTerm.term } }


    override suspend fun saveTerm(term: String) =
        termDao.addTerm(DbTerm(term.hashCode().toLong(), term))

}