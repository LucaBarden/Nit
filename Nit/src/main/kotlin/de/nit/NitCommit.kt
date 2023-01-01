package de.nit

import com.google.common.hash.Hashing
import java.io.File
import de.nit.Constants.COMMIT_FOLDER_PATH
import de.nit.Constants.CONFIG_FILE_PATH
import de.nit.Constants.INDEX_FILE_PATH
import de.nit.Constants.LAST_COMMIT_FILE_PATH
import de.nit.Constants.LOG_FILE_PATH
import de.nit.Constants.SEPERATOR
import org.zeroturnaround.exec.ProcessExecutor
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.LocalDateTime
import kotlin.math.absoluteValue


object NitCommit {
    fun generateCommit(
        currentCommitHash: String,
        commitMessage: String,
        lastCommitFile: File
    ) {
        generateCommitFiles(currentCommitHash)
        val lastCommitHash = File(LAST_COMMIT_FILE_PATH).readText()
        saveCommitLog(commitMessage, currentCommitHash, lastCommitHash)
        lastCommitFile.writeText(currentCommitHash)
        println("Changes are committed.")
        clearIndex()
    }

    fun clearIndex() {
        File(INDEX_FILE_PATH).writeText("")
    }

    private fun saveCommitLog(commitMessage: String, currentCommitHash: String, lastCommitHash: String = "") {
        val username = File(CONFIG_FILE_PATH).readText()
        val logFile = File(LOG_FILE_PATH)
        val logContent = logFile.readText()
        var fileDiff = ""
        if (lastCommitHash != "") {
            fileDiff = getFileDiffs()
        }
        val newCommitLog =
            "commit $currentCommitHash\nAuthor: $username\nDate: ${LocalDateTime.now()}\n$commitMessage\n\n$fileDiff\n"
        logFile.writeText(newCommitLog + "\n" + logContent)
    }

    private fun getFileDiffs(): String {
        var fileDiff: String = ""
        val lastCommitHash = File(LAST_COMMIT_FILE_PATH).readText()
        val lastCommitFolder = File(COMMIT_FOLDER_PATH + lastCommitHash + SEPERATOR)
        val indexFile = File(INDEX_FILE_PATH)
        for (fileName in indexFile.readLines()) {
            fileDiff += ProcessExecutor().command("diff", "-c", fileName, lastCommitFolder.path + SEPERATOR + fileName).readOutput(true)
                .execute().outputString()
            println(fileDiff)
        }
        return fileDiff
    }

    private fun generateCommitFiles(currentCommitHash: String) {
        val currentCommitDir = File(COMMIT_FOLDER_PATH +  currentCommitHash + SEPERATOR)
        currentCommitDir.mkdir()

        for (fileName in File(".").listFiles()!!.filter { ".git" !in it.path }.filter { ".nit" !in it.path }.map { it.path }) {
            File(fileName).copyRecursively(File(currentCommitDir.path + SEPERATOR + fileName))
        }
    }

    fun getCommitHash(trackFile: File): String {
        var fileContents = ""
        for (fileName in trackFile.readLines()) {
            fileContents += File(fileName).readText()
        }
        val hashFunction = Hashing.sha256()
        val hashCode = hashFunction.newHasher().putString(fileContents, Charset.defaultCharset()).hash()
        return BigInteger(hashCode.asBytes()).abs().toString(16).substring(0,20)
    }
}
