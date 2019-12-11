package io.demo.fedchenko.giphyclient.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbGif(
    @PrimaryKey(autoGenerate = false) val id: Long?,
    @ColumnInfo(name = "id_str") val id_str: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "user_name") val userName: String?,
    @ColumnInfo(name = "import_date_time") val importDateTime: String?,

    @ColumnInfo(name = "original_width") val originalWidth: Int?,
    @ColumnInfo(name = "original_height") val originalHeight: Int?,
    @ColumnInfo(name = "original_url") val originalUrl: String?,
    @ColumnInfo(name = "original_size") val originalSize: Int?,

    @ColumnInfo(name = "preview_width") val previewWidth: Int?,
    @ColumnInfo(name = "preview_height") val previewHeight: Int?,
    @ColumnInfo(name = "preview_url") val previewUrl: String?,
    @ColumnInfo(name = "preview_size") val previewSize: Int?
)