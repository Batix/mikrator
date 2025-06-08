import com.vanniktech.maven.publish.SonatypeHost

plugins {
  jacoco
  `java-library`
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.maven.publish)
}

repositories {
  mavenCentral()
}

dependencies {
  api(libs.liquibase.core)

  implementation(libs.kotlin.logging)
}

//
// -- compilation --
//

kotlin {
  explicitApi()

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

  doLast {
    val reportFile = project.file("${reports.html.outputLocation.get()}/index.html")
    println("test HTML report at file:///${reportFile.absolutePath.replace("\\", "/")}")
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

  classDirectories.setFrom(files(classDirectories.files.map {
    fileTree(it) {
      // ignore logger creation lambdas
      exclude("**/*\$logger$*.class")
    }
  }))

  doLast {
    val reportFile = project.file("${reports.html.outputLocation.get()}/index.html")
    println("coverage HTML report at file:///${reportFile.absolutePath.replace("\\", "/")}")

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

//
// -- publishing --
//

mavenPublishing {
  publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
  signAllPublications()

  pom {
    name.set("Mikrator")
    description.set("A Liquibase wrapper for easy usage and changelogs as code.")
    inceptionYear.set("2025")
    url.set("https://github.com/Batix/mikrator")

    licenses {
      license {
        name.set("MIT")
        url.set("https://opensource.org/licenses/MIT")
      }
    }

    developers {
      developer {
        name.set("David Kirstein")
        email.set("dak@batix.com")
        organization.set("Batix")
        organizationUrl.set("https://www.batix.de/")
      }
    }

    scm {
      connection.set("scm:git:https://github.com/Batix/mikrator.git")
      developerConnection.set("scm:git:https://github.com/Batix/mikrator.git")
      url.set("https://github.com/Batix/mikrator")
    }
  }
}
