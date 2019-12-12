package io.demo.fedchenko.giphyclient.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TermDao {
    @Query("SELECT * FROM dbterm")
    fun getTermsFlow(): Flow<List<DbTerm>>

    @Insert
    suspend fun addTerm(term: DbTerm)
}