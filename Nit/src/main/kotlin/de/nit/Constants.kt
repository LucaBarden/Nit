package de.nit

import java.io.File

object Constants {
    private const val NIT_FOLDER_NAME = ".nit"
    private const val LOG_FILE_NAME = "log"
    private const val LAST_COMMIT_FILE_NAME = "HEAD"
    private const val CONFIG_FILE_NAME = "config"
    private const val INDEX_FILE_NAME = "index"


    val SEPERATOR: String = File.separator
    val CURRENT_WORKING_DIR: String = System.getProperty("user.dir")

    val NIT_FOLDER_PATH = "$NIT_FOLDER_NAME$SEPERATOR"
    val COMMIT_FOLDER_PATH = "${NIT_FOLDER_PATH}commits$SEPERATOR"
    val INDEX_FILE_PATH = "$NIT_FOLDER_PATH$INDEX_FILE_NAME"
    val CONFIG_FILE_PATH = "$NIT_FOLDER_PATH$CONFIG_FILE_NAME"
    val LAST_COMMIT_FILE_PATH = "$NIT_FOLDER_PATH$LAST_COMMIT_FILE_NAME"
    val LOG_FILE_PATH = "$NIT_FOLDER_PATH$LOG_FILE_NAME"
}