package io.demo.fedchenko.giphyclient.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GifDao {
    @Query("SELECT * FROM dbgif")
    fun getAll(): List<DbGif>

    @Insert
    fun insertAll(vararg gifs: DbGif)

    @Delete
    fun delete(gif: DbGif)
}