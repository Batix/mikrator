package com.batix.mikrator

import liquibase.changelog.DatabaseChangeLog
import liquibase.database.Database
import liquibase.database.DatabaseFactory
import liquibase.parser.SnapshotParserFactory
import liquibase.sdk.resource.MockResourceAccessor
import liquibase.serializer.ChangeLogSerializerFactory
import liquibase.serializer.SnapshotSerializerFactory
import liquibase.snapshot.DatabaseSnapshot
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.file.Path
import java.sql.Connection
import kotlin.io.path.name
import kotlin.io.path.outputStream
import kotlin.io.path.reader

public fun openDatabase(url: String, username: String? = null, password: String? = null): Database =
  DatabaseFactory.getInstance().openDatabase(url, username, password, null, null)

/**
 * [javaSqlConnection] won't be closed (via [Database.close]) when [close] is called.
 */
public fun openDatabase(javaSqlConnection: Connection): Database =
  DatabaseFactory.getInstance().findCorrectDatabaseImplementation(NonClosingJdbcConnection(javaSqlConnection))

public enum class FileFormat {
  JSON, YAML
}

public fun DatabaseChangeLog.serializeTo(output: OutputStream, format: FileFormat) {
  ChangeLogSerializerFactory.getInstance()
    .getSerializer("." + format.name.lowercase())
    .write(changeSets, output)
}

public fun DatabaseSnapshot.serialize(format: FileFormat): String {
  return SnapshotSerializerFactory.getInstance()
    .getSerializer("." + format.name.lowercase())
    .serialize(this, true)
}

public fun DatabaseSnapshot.serializeTo(path: Path, format: FileFormat) {
  path.outputStream().use { ostream ->
    SnapshotSerializerFactory.getInstance()
      .getSerializer("." + format.name.lowercase())
      .write(this, ostream)
  }
}

public fun loadSnapshot(
  snapshotContent: String,
  format: FileFormat,
): DatabaseSnapshot {
  val nameArg = "." + format.name.lowercase()
  val accessor = MockResourceAccessor().apply {
    setContent(nameArg, snapshotContent)
  }

  return SnapshotParserFactory.getInstance()
    .getParser(nameArg, accessor)
    .parse(nameArg, accessor)
}

public fun loadSnapshot(
  path: Path,
  format: FileFormat? = null,
  charset: Charset = Charsets.UTF_8,
): DatabaseSnapshot {
  return path.reader(charset).use { reader ->
    val accessor = MockResourceAccessor().apply {
      setContent(path.name, reader.readText())
    }

    val nameArg = if (format != null) "." + format.name.lowercase() else path.name

    SnapshotParserFactory.getInstance()
      .getParser(nameArg, accessor)
      .parse(path.name, accessor)
  }
}
