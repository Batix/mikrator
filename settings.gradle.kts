plugins {
  // https://plugins.gradle.org/plugin/org.gradle.toolchains.foojay-resolver-convention
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "mikrator"

include(":lib")
project(":lib").apply {
  name = "mikrator"
}

include(":tests")
project(":tests").apply {
  name = "mikrator-tests"
}
