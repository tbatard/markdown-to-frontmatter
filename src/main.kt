import java.io.File

const val directory = "/Users/pivotal/go/src/github.com/vmware-tanzu/velero/site/content/docs/"

fun main() {
    parseDirectory(directory)
}

private fun parseDirectory(directory: String) {
    var totalFiles = 0
    var successFiles = 0
    var skippedFiles = 0
    var missedFiles = 0
    File(directory).walk().forEach lit@{
        if (it.name.contains("_index.md")) {
            println("Skipping _index.md")
            skippedFiles++
            return@lit
        }

        if (it.isFile && it.extension.equals("md")) {
            totalFiles++
            val lines = it.readLines()
            var firstLine = lines[0]
            var linesToReplace = firstLine
            if (firstLine.isBlank()) {
                firstLine = lines[1]
                linesToReplace += "\n$firstLine"
            }

            val validLine = "((\uFEFF)?#+\\s)([\\w\\S ]+)"
            val regex = validLine.toRegex()
            if (regex.matches(firstLine)) {
                val title = regex.find(firstLine)!!.groupValues[3]
                successFiles++

                val newTitle = """
                    ---
                    title: "$title"
                    layout: docs
                    ---
                """.trimIndent()

                val newText = it.readText().replace(linesToReplace, newTitle)
                it.writeText(newText)
            } else {
                missedFiles++
                System.err.println("File $it does not start with a title: $firstLine")
            }
        }
    }

    println()
    println("Finished processing directory $directory")
    println("Success: $successFiles")
    println("Missed: $missedFiles")
    println("Skipped: $skippedFiles")
    println("Total: $totalFiles")
}