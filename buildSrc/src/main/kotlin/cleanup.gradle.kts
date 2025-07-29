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
            oldValue = "rootProject.name = \"compose-android-template\"",
            newValue = "rootProject.name = \"$name\""
        )

        changePackageName(
            owner = owner,
            name = name
        )

        // cleanup the cleanup :)
        file(path = ".github/workflows/cleanup.yml").delete()
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

fun File.replace(oldValue: String, newValue: String) {
    writeText(text = readText().replace(oldValue, newValue))
}

fun srcDirectories() = projectDir.listFiles()!!
    .filter { it.isDirectory && it.name != "buildSrc" }
    .flatMap { it.listFiles()!!.filter { it.isDirectory && it.name == "src" } }

@Suppress("NestedBlockDepth")
/**
 * Change the package name of the project.
 * @param owner The owner of the repository, usually the GitHub username.
 * @param name The name of the repository, usually the project name.
 */
fun changePackageName(owner: String, name: String) {
    srcDirectories().forEach {
        it.walk().filter {
            it.isFile && (it.extension == "kt" || it.extension == "kts" || it.extension == "xml")
        }.forEach {
            it.replace("com.codermp.composeandroidtemplate", "com.$owner.$name")
        }
    }
    srcDirectories().forEach { srcDir ->
        srcDir
            .listFiles()!!
            .filter { it.isDirectory } // down to src/main, src/test, etc.
            .flatMap {
                it.listFiles()!!.filter { it.isDirectory } // down to src/main/java, etc.
            }
            .forEach { javaDir ->
                val oldPackageDir = File(javaDir, "com/codermp/composeandroidtemplate")

                if (oldPackageDir.exists()) {
                    val newPackageDir = File(javaDir, "com/$owner/$name")

                    // Create parent directories for new package
                    newPackageDir.parentFile.mkdirs()

                    // Move files instead of renaming directory
                    if (oldPackageDir.isDirectory) {
                        oldPackageDir.listFiles()?.forEach { file ->
                            val targetFile = File(newPackageDir, file.name)
                            if (file.isDirectory) {
                                file.copyRecursively(targetFile)
                            } else {
                                file.copyTo(targetFile)
                            }
                        }
                    }

                    // Only delete old structure after successful copy
                    if (newPackageDir.exists() && newPackageDir.listFiles()?.isNotEmpty() == true) {
                        oldPackageDir.deleteRecursively()
                    }
                }
            }
    }
}