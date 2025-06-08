# Mikrator

> [!WARNING]
> 🚧 Alpha version - use with caution! 🚧

This is a Kotlin wrapper around Liquibase OSS ([website](https://www.liquibase.com/)). The goal is to define changelogs
in code rather than text files, with a helpful DSL. Running commands like update or diff is also made easier.

Here is a small example what it looks like:

```kotlin
val config = Config {
  sqlShowAtLogLevel = Level.INFO
}

val changeLog = ChangeLog {
  changeSet("1-create", "John Doe") {
    changes {
      createTable("tbl") {
        column("id", "INT")
      }
    }
  }

  changeSet("2-insert", "John Doe") {
    changes {
      insert("tbl") {
        column("id") { numericValue(1) }
      }

      tagDatabase("v1")
    }
  }

  changeSet("3-insert", "John Doe") {
    changes {
      insert("tbl") {
        column("id") { numericValue(2) }
      }
    }

    rollback {
      delete("tbl", where = "id = 2")
    }
  }
}

Mikrator(openDatabase(sqlConn), config).use { mikrator ->
  mikrator.update(changeLog)

  if (weNeedToGoBack && mikrator.tagExists("v1")) {
    mikrator.rollback(changeLog, "v1")
  }

  logger.trace { "db snapshot:\n${mikrator.snapshot().serialize(FileFormat.YAML)}" }
}
```

You'll need [`com.batix:mikrator:...`]() and the appropriate JDBC database driver. Something like:

```kotlin
dependencies {
  // https://mvnrepository.com/artifact/com.batix/mikrator
  implementation("com.batix:mikrator:...")

  // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
  runtimeOnly("org.mariadb.jdbc:mariadb-java-client:...")

  // https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
  runtimeOnly("org.xerial:sqlite-jdbc:...")
}
```
