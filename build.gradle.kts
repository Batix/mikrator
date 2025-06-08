plugins {
  base
  alias(libs.plugins.kotlin.jvm) apply false
}

//
// -- determine version from git --
//

fun determineVersionFromGit(): String {
  val stdOut = try {
    providers.exec {
      commandLine(
        "git", "describe", // find the most recent tag and derive a version string from it
        "--dirty", // append -dirty if there are local modifications
        "--tags", // also use lightweight tags (in addition to annotated tags)
        "--match", "v*.*.*", // only consider tags in the form vx.y.z
        "--always" // just use the abbreviated commit if no tags are found
      )
    }.standardOutput.asText.get()
  } catch (e: Exception) {
    project.logger.warn("Cannot determine version via git describe, using 'unknown'.", e)
    return "unknown"
  }

  return stdOut.trim().replace(Regex("^v"), "")
}

var determinedVersion = determineVersionFromGit()

val printVersion by tasks.registering {
  group = "help"
  description = "Prints the current version as calculated by determineVersionFromGit()."

  doLast {
    println(determinedVersion)
  }
}

allprojects {
  group = "com.batix"
  version = determinedVersion
}
