/**
 * Gradle plugin to cleanup the template after it has been forked. It register a single `templateCleanup`
 * task that is designed to run from CI. It:
 * - renames the root project
 * - replaces the maven coordinates with coordinates based on the Github repository where the
 * template is forked
 * - changes the package name
 * - changes the Android application ID
 * - cleanups after itself by removing the Github action and this plugin
 */
check(rootProject.name == name) {
    "The cleanup plugin should be applied to the root project and not $name"
}

tasks.register("templateCleanup") {
    doLast {
        val repository = System.getenv("GITHUB_REPOSITORY")
            ?: error("No GITHUB_REPOSITORY environment variable. Are you running from Github Actions?")

        val (owner, name) = repository.split("/").let {
            it[0].sanitized() to it[1].sanitized()
        }

        file(path = "gradle/libs.versions.toml").replace(
            oldValue = "com.codermp.composeandroidtemplate",
            newValue = "com.$owner.$name"
        )
        file(path = "settings.gradle.kts").replace(
            oldValue = "rootProject.name = (\"compose-android-template\")",
            newValue = "rootProject.name = (\"$name\")"
        )
        file(path = "gradle.properties").replace(
            oldValue = "com.codermp.composeandroidtemplate",
            newValue = "com.$owner.$name"
        )

        changePackageName(
            owner = owner,
            name = name
        )

        // cleanup the cleanup :)
        file(path = ".github/workflows/cleanup.yaml").delete()
        file(path = "build.gradle.kts").replace(
            oldValue = "    cleanup\n",
            newValue = ""
        )
        file(path = "buildSrc/src/main/kotlin/cleanup.gradle.kts").delete()
    }
}

fun String.sanitized() = replace(
    regex = Regex(pattern = "[^A-Za-z0-9]"),
    replacement = ""
).lowercase()

fun File.replace(regex: Regex, replacement: String) {
    writeText(text = readText().replace(regex, replacement))
}

fun File.replace(oldValue: String, newValue: String) {
    writeText(text = readText().replace(oldValue, newValue))
}

fun srcDirectories() = projectDir.listFiles()!!
    .filter { it.isDirectory && it.name != "buildSrc" }
    .flatMap { it.listFiles()!!.filter { it.isDirectory && it.name == "src" } }

/**
 * Change the package name of the project.
 */
fun changePackageName(owner: String, name: String) {
    srcDirectories().forEach {
        it.walk().filter {
            it.isFile && (it.extension == "kt" || it.extension == "kts" || it.extension == "xml")
        }.forEach {
            it.replace("com.codermp.composeandroidtemplate", "com.$owner.$name")
        }
    }
    srcDirectories().forEach {
        it.listFiles()!!.filter { it.isDirectory } // down to src/main
            .flatMap { it.listFiles()!!.filter { it.isDirectory } } // down to src/main/java
            .forEach {
                val newDir = File(it, "com/$owner/$name")
                newDir.parentFile.mkdirs()
                File(it, "com/codermp/composeandroidtemplate").renameTo(newDir)
                File(it, "com/codermp").deleteRecursively()
            }
    }
}