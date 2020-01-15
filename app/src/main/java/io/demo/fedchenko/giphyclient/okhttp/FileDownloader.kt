package io.demo.fedchenko.giphyclient.okhttp

import java.io.File

interface FileDownloader {
    suspend fun getFile(url:String): File
}