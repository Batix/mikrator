plugins {
  jacoco
  alias(libs.plugins.kotlin.jvm)
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":mikrator"))

  testImplementation(libs.bundles.kotest)
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.testcontainers.mariadb)

  testRuntimeOnly(libs.junit.platform.launcher)
  testRuntimeOnly(libs.liquibase.slf4j)
  testRuntimeOnly(libs.logback.classic)
  testRuntimeOnly(libs.mariadb.client)
  testRuntimeOnly(libs.sqlite.jdbc)
}

//
// -- compilation --
//

kotlin {
  jvmToolchain {
    libs.versions.java.map { JavaVersion.toVersion(it) }
  }
}

//
// -- unit tests --
//

/*
task: verification > test
report: build/reports/tests/test/index.html
 */

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

tasks.withType<Test> {
  useJUnitPlatform()

  val reportFile = reports.html.outputLocation.file("index.html")

  doLast {
    println("test HTML report at file:///${reportFile.get().asFile.absolutePath.replace("\\", "/")}")
  }
}

//
// -- source coverage --
//

/*
task: verification > jacocoTestReport (runs automatically after test)
report: build/reports/jacoco/test
https://docs.gradle.org/current/userguide/jacoco_plugin.html
 */

tasks.test {
  finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
  dependsOn(tasks.test) // tests are required to run before generating the report

  reports {
    html.required.set(true)
    xml.required.set(true)
    csv.required.set(false)
  }

  additionalSourceDirs(project.files(project(":mikrator").sourceSets.main.get().allSource.srcDirs))
  additionalClassDirs(project(":mikrator").sourceSets.main.get().output)

  classDirectories.setFrom(files(classDirectories.files.map {
    fileTree(it) {
      // ignore logger creation lambdas
      exclude("**/*\$logger$*.class")
    }
  }))

  val reportFile = reports.html.outputLocation.file("index.html")

  doLast {
    println("coverage HTML report at file:///${reportFile.get().asFile.absolutePath.replace("\\", "/")}")

    val xmlFile = reports.xml.outputLocation.get()
    val parser = groovy.xml.XmlParser()

    parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
    parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

    val root = parser.parse(xmlFile.asFile)
    val counters = root.get("counter") as groovy.util.NodeList
    val counterInstruction = counters.filterIsInstance<groovy.util.Node>().single {
      it.attribute("type") == "INSTRUCTION"
    }

    val instructionsMissed = counterInstruction.attribute("missed").toString().toInt()
    val instructionsCovered = counterInstruction.attribute("covered").toString().toInt()
    val instructionsTotal = instructionsCovered + instructionsMissed
    val instructionsPercent = instructionsCovered.toFloat() / instructionsTotal.toFloat() * 100f

    println("Coverage: ${"%.2f".format(instructionsPercent)} % ($instructionsCovered / $instructionsTotal instructions)")
  }
}
