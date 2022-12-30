package de.nit


import de.nit.Constants.COMMIT_FOLDER_PATH
import de.nit.Constants.CONFIG_FILE_PATH
import de.nit.Constants.CURRENT_WORKING_DIR
import de.nit.Constants.INDEX_FILE_PATH
import de.nit.Constants.LAST_COMMIT_FILE_PATH
import de.nit.Constants.LOG_FILE_PATH
import de.nit.Constants.NIT_FOLDER_PATH
import de.nit.Constants.SEPERATOR
import de.nit.NitCommit.getCommitHash
import org.zeroturnaround.exec.ProcessExecutor
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


object Nit {

    fun runProgram(args: Array<String>) {
        val mainCommand = getFirstArgumentOrEmpty(args)
        if (mainCommand.isEmpty())
            return

        if (mainCommand == "init") {
            initCommand()
            return
        }

        if (!checkIfRepo()) {
            System.err.println("Current Directory is not a Nit Repository")
            System.err.println("Use `nit init` to initilize one")
            return
        }

        executeCommand(mainCommand, args)
    }

    private fun getFirstArgumentOrEmpty(args: Array<String>): String {
        return if (args.isNotEmpty())
            args[0]
        else {
            printHelp()
            ""
        }
    }

    private fun executeCommand(mainCommand: String, arguments: Array<String>) {
        when (mainCommand) {
            "--help" -> printHelp()
            "config" -> configCommand(arguments)
            "add" -> addCommand(arguments)
            "commit" -> commitCommand(arguments)
            "log" -> logCommand()
            "checkout" -> checkoutCommand(arguments)
            "terminate" -> terminateCommand()
            "test" -> testStuffCommand(arguments)
            else -> invalidCommand(arguments[0])
        }
    }

    private fun testStuffCommand(arguments: Array<String>) {
        val output = ProcessExecutor().command("diff", "test1", "test2").readOutput(true).execute().outputString()
    }

    private fun terminateCommand() {
        if (!checkIfRepo()) {
            System.err.println("The current directory is not a Nit repository.")
            return
        }
        println("This will delete all versions logged in this repository.")
        println("Are you sure you want to continue? Y(es)/N(o)")
        val userOption = readln().lowercase()
        if (userOption == "y" || userOption == "yes") {
            File(NIT_FOLDER_PATH).deleteRecursively()
            println("Nit has been removed from this folder")
            return
        }
    }

    private fun initCommand() {
        if (!checkIfRepo()) {
            initNitDirectories()
            return
        } else {
            System.err.println("This is already a Nit repository")
            return
        }
    }

    private fun checkIfRepo(): Boolean {
        return File(NIT_FOLDER_PATH).exists()
    }


    private fun initNitDirectories() {
        val nitFolder = File(NIT_FOLDER_PATH)
        val commitsFolder = File(COMMIT_FOLDER_PATH)

        if (!nitFolder.exists())
            nitFolder.mkdir()
        if (!commitsFolder.exists())
            commitsFolder.mkdir()

        File(LAST_COMMIT_FILE_PATH).createNewFile()
        File(LOG_FILE_PATH).createNewFile()
        File(INDEX_FILE_PATH).createNewFile()
        File(CONFIG_FILE_PATH).createNewFile()

    }

    private fun invalidCommand(enteredCommand: String) {
        println("'$enteredCommand' is not a Nit command.")
    }

    private fun checkoutCommand(args: Array<String>) {
        if (args.size < 2) {
            println("Commit id was not passed.")
            return
        }
        val givenCommitHash = args[1]
        val pathToLookFor = COMMIT_FOLDER_PATH + givenCommitHash
        val hashFolder = File(pathToLookFor)

        if (!hashFolder.exists()) {
            println("Commit does not exist.")
            return
        }
        val committedFiles = hashFolder.listFiles()!!.toMutableList()
        for (committedFile in committedFiles) {
            val restoredFilePath = CURRENT_WORKING_DIR + SEPERATOR + committedFile.name
            committedFile.copyTo(File(restoredFilePath), overwrite = true)
        }

        println("Switched to commit $givenCommitHash.")

    }

    private fun logCommand() {
        val logFile = File(LOG_FILE_PATH)
        if (logFile.length() == 0L) {
            println("No commits yet.")
            return
        }
        logFile.readLines().forEach(::println)
    }


    private fun commitCommand(args: Array<String>) {
        if (args.size < 2) {
            println("Message was not passed.")
            return
        }
        val commitMessage = args[1]
        val trackFile = File(INDEX_FILE_PATH)
        if (checkForCommittable(trackFile)) return

        val currentCommitHash = getCommitHash(trackFile)
        val lastCommitFile = File(LAST_COMMIT_FILE_PATH)
        val lastCommitHash = lastCommitFile.readText()

        if (checkForUnchangedRepo(currentCommitHash, lastCommitHash)) return

        NitCommit.generateCommit(currentCommitHash, trackFile, commitMessage, lastCommitFile)

    }

    private fun checkForUnchangedRepo(currentCommitHash: String, lastCommitHash: String): Boolean {
        if (currentCommitHash == lastCommitHash) {
            println("Nothing to commit.")
            return true
        }
        return false
    }

    private fun checkForCommittable(trackFile: File): Boolean {
        if (trackFile.length() == 0L) {
            println("Nothing to commit.")
            return true
        }
        return false
    }


    private fun addCommand(args: Array<String>) {
        if (args.size < 2) {
            outputTrackedFiles()
        } else {
            val fileNameToAdd = args[1]
            val fileToAdd = File(fileNameToAdd)
            if (!fileToAdd.exists()) {
                println("Can't find '$fileNameToAdd'.")
                return
            }
            addFile(fileToAdd)
        }
    }

    private fun addFile(fileToAdd: File) {
        val trackFile = File(INDEX_FILE_PATH)
        if (trackFile.canWrite()) {
            val fileName = fileToAdd.name
            trackFile.appendText(fileName + '\n')
            println("The file '$fileName' is tracked.")
        } else
            println("Could not write to index file")
    }

    private fun outputTrackedFiles() {
        val trackFile = File(INDEX_FILE_PATH)
        if (trackFile.exists() && trackFile.length() != 0L) {
            println("Tracked files:")
            for (file in trackFile.readLines())
                println(file)
        } else {
            println("Add a file to the index.")
        }
    }

    private fun configCommand(args: Array<String>) {
        val configFile = File(CONFIG_FILE_PATH)
        if (args.size == 1 && configFile.length() == 0L)
            println("Please, tell me who you are.")
        else if (args.size == 1) {
            if (configFile.canRead()) {
                val username = configFile.readText()
                println("The username is $username.")
            } else {
                println("Could not read username from config file")
            }

        } else if (args.size == 2) {
            val username = args[1]
            configFile.createNewFile()
            if (configFile.canWrite()) {
                configFile.writeText(username)
                println("The username is $username.")
            } else
                println("Could not open config file")
        }
    }

    private fun printHelp() {
        println(
            """
                    |These are the available Nit commands:
                    |init                Initializes a Nit Repository
                    |config              Get and set a username.
                    |add                 Add a file to the Staged File Index.
                    |commit              Commit Staged Files.
                    |checkout            Restore the state of files of a given commit.
                    |terminate           Removes Nit tracking from the current repository.
                """.trimMargin()
        )
    }
}
