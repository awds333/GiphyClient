package io.demo.fedchenko.giphyclient.repository

import kotlinx.coroutines.flow.Flow

interface TermsManager {
    fun getTerms(): Flow<List<String>>
    suspend fun saveTerm(term: String)
}