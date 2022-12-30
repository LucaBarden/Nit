package de.nit

import java.io.File
import de.nit.Constants.COMMIT_FOLDER_PATH
import de.nit.Constants.CONFIG_FILE_PATH
import de.nit.Constants.LOG_FILE_PATH
import de.nit.Constants.SEPERATOR


object NitCommit {
    fun generateCommit(
        currentCommitHash: String,
        trackFile: File,
        commitMessage: String,
        lastCommitFile: File
    ) {
        generateCommitFiles(currentCommitHash, trackFile)

        saveCommitLog(commitMessage, currentCommitHash)
        lastCommitFile.writeText(currentCommitHash)
        println("Changes are committed.")
    }

    private fun saveCommitLog(commitMessage: String, currentCommitHash: String) {
        val username = File(CONFIG_FILE_PATH).readText()
        val logFile = File(LOG_FILE_PATH)
        val logContent = logFile.readText()
        val newCommitLog = "commit $currentCommitHash\nAuthor: $username\n$commitMessage"
        logFile.writeText(newCommitLog + "\n" + logContent)
    }

    private fun generateCommitFiles(currentCommitHash: String, trackFile: File) {
        val currentCommitDir = File(COMMIT_FOLDER_PATH + SEPERATOR + currentCommitHash + SEPERATOR)
        currentCommitDir.mkdir()
        for (fileName in trackFile.readLines()) {
            File(fileName).copyTo(File(currentCommitDir.path + SEPERATOR + fileName))
        }
    }
}
