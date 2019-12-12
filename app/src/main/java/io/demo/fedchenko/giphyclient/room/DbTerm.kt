package io.demo.fedchenko.giphyclient.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbTerm (
    @PrimaryKey(autoGenerate = false) val id: Long?,
    @ColumnInfo(name = "term") val term: String?
)
