package io.demo.fedchenko.giphyclient.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GifDao {
    @Query("SELECT * FROM dbgif")
    fun getAll(): Flow<List<DbGif>>

    @Insert
    suspend fun insertAll(vararg gifs: DbGif)

    @Delete
    suspend fun delete(gif: DbGif)
}