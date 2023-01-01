# Nit - Not (G)it - VCS Client written in Kotlin
## Simple VCS inspired by the Kotlin VCS Learning Project



### Basic Usage:
```console
    ./build.sh
    kotlin ./Nit/build/libs/Nit.jar
```


### TODOS:
- Refactor Code
- Add Possibility to add Folders (In Generall multiple Files at once)
- Add Status Command to list unstaged Changes
- Maybe add File Compression and Decompression for Commits in the end

### DONE:
- Make Initialization a seperate Command
- Add Command to unstage Files again
- Add Command to Remove `Nit` from a Folder again
- Changed some phrasing and naming of folders and files
- Add Diff of Commits to Log (More Detailed Logs)
- Commit History Format Data - Author - Commit Msg - Commit Hash
- Add Command reset to reset changes that have been made
- Rework Hashing with Guava


### References:
 - [Kotlin VCS Learning Project](https://hyperskill.org/projects/177?track=18)
 - [My Implementation during the Project](https://github.com/LucaBarden/kotlin-learning-path/tree/master/Version%20Control%20System)
 - [zt-exec Package for executing shell commands](https://github.com/zeroturnaround/zt-exec)
