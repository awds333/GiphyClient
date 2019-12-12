package io.demo.fedchenko.giphyclient.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DbGif::class, DbTerm::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun gifDao(): GifDao
    abstract fun termDao(): TermDao
}