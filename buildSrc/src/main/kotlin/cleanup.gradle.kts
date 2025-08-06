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

        val nameCasePreserved = repository.split("/")[1].sanitizedPreservingCase()

        file(path = "gradle/libs.versions.toml").replace(
            oldValue = "com.codermp.composeandroidtemplate",
            newValue = "com.$owner.$name"
        )
        file(path = "settings.gradle.kts").replace(
            oldValue = "rootProject.name = \"compose-android-template\"",
            newValue = "rootProject.name = \"$name\""
        )

        changePackageName(owner = owner, name = name)
        changeManifestFile(name = nameCasePreserved)
        changeAppClassFile(name = nameCasePreserved)
        changeThemeFile(name = nameCasePreserved)
        changeResourceFiles(name = nameCasePreserved)

        // cleanup the cleanup :)
        file(path = ".github/workflows/cleanup.yml").delete()
        file(path = "build.gradle.kts").replace(
            oldValue = "    cleanup\n",
            newValue = ""
        )
        file(path = "buildSrc").deleteRecursively()
    }
}

/**
 * [String] extension function that sanitizes a string by removing all non-alphanumeric characters and
 * converting it to lowercase.
 */
fun String.sanitized() = replace(
    regex = Regex(pattern = "[^A-Za-z0-9]"),
    replacement = ""
).lowercase()

/**
 * [String] extension function that sanitizes a string by removing all non-alphanumeric characters
 * while preserving the case. This is useful for cases where the original case of the string is
 * important, such as in class names.
 */
fun String.sanitizedPreservingCase() = split(regex = Regex("[^A-Za-z0-9]"))
    .filter { it.isNotEmpty() }
    .joinToString("")

/**
 * [File] extension function that replaces all occurrences of `oldValue` with `newValue` in the file.
 * @param oldValue The string to be replaced.
 * @param newValue The string to replace with.
 */
fun File.replace(oldValue: String, newValue: String) {
    writeText(text = readText().replace(oldValue, newValue))
}

/**
 * Returns a list of source directories in the project.
 * It excludes the `buildSrc` directory to avoid unnecessary processing.
 */
fun srcDirectories() = projectDir
    .listFiles()!!
    .filter { it.isDirectory && it.name != "buildSrc" }
    .flatMap {
        it.listFiles()!!.filter { it.isDirectory && it.name == "src" }
    }

@Suppress("NestedBlockDepth")
/**
 * Change the package name of the project.
 * @param owner The owner of the repository, usually the GitHub username.
 * @param name The name of the repository, usually the project name.
 */
fun changePackageName(owner: String, name: String) {
    srcDirectories().forEach { directory ->
        directory
            .walk()
            .filter {
                it.isFile && (it.extension == "kt" || it.extension == "kts" || it.extension == "xml")
            }
            .forEach { file ->
                file.replace(
                    oldValue = "com.codermp.composeandroidtemplate",
                    newValue = "com.$owner.$name"
                )
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

/**
 * Changes the Android manifest file and related app configurations.
 * @param name The name of the repository, usually the project name.
 */
fun changeManifestFile(name: String) {
    // Update AndroidManifest.xml file
    projectDir
        .walk()
        .filter { it.name == "AndroidManifest.xml" }
        .forEach { manifestFile ->
            manifestFile.replace(
                oldValue = "android:name=\".app.TemplateApp\"",
                newValue = "android:name=\".app.${name}App\""
            )
            manifestFile.replace(
                oldValue = "android:theme=\"@style/Theme.ComposeAndroidTemplate.Starting\"",
                newValue = "android:theme=\"@style/Theme.$name.Starting\"",
            )
        }
}

/**
 * Change the main application class file to reflect the new project name.
 * @param name The name of the repository, usually the project name.
 */
fun changeAppClassFile(name: String) {
    // Rename TemplateApp class file
    srcDirectories()
        .forEach { srcDir ->
            srcDir
                .walk()
                .filter { it.isFile && it.name == "TemplateApp.kt" }
                .forEach { templateAppFile ->
                    // Update class name inside the file
                    templateAppFile.replace(
                        oldValue = "class TemplateApp", newValue = "class ${name}App"
                    )
                    templateAppFile.replace(
                        oldValue = "this@TemplateApp", newValue = "this@${name}App"
                    )

                    // Rename the file itself
                    val newFile = File(templateAppFile.parent, "${name}App.kt")
                    templateAppFile.renameTo(newFile)
                }
        }
}

/**
 * Change the Compose theme file to reflect the new project name.
 * @param name The name of the repository, usually the project name.
 */
fun changeThemeFile(name: String) {
    srcDirectories()
        .forEach { srcDir ->
            srcDir
                .walk()
                .filter { it.isFile && it.name == "Theme.kt" }
                .forEach { themeFile ->
                    themeFile.replace(
                        oldValue = "ComposeAndroidTemplateTheme", newValue = "${name}Theme"
                    )
                }
        }
}

/**
 * Change the resource files such as themes.xml, strings.xml, and splash.xml.
 * @param name The name of the repository, usually the project name.
 */
fun changeResourceFiles(name: String) {
    // Update themes.xml file
    projectDir
        .walk()
        .filter { it.name == "themes.xml" }
        .forEach { themesFile ->
            themesFile.replace(
                oldValue = "Theme.ComposeAndroidTemplate", newValue = "Theme.$name"
            )
        }

    // Update strings.xml file
    projectDir
        .walk()
        .filter { it.name == "strings.xml" }
        .forEach { stringsFile ->
            stringsFile.replace(
                oldValue = "Compose Android Template", newValue = name
            )
        }

    // Update splash.xml file
    projectDir
        .walk()
        .filter { it.name == "splash.xml" }
        .forEach { splashFile ->
            splashFile.replace(
                oldValue = "Theme.ComposeAndroidTemplate.Starting",
                newValue = "Theme.${name}.Starting"
            )
        }
}