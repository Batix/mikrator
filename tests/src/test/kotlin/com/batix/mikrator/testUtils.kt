package com.batix.mikrator

import org.testcontainers.containers.MariaDBContainer
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.logging.Level

/**
 * Won't write to disk, resides exclusively in memory.
 */
private const val SQLITE_MEMORY_DB = "jdbc:sqlite::memory:"

/**
 * May write to etilqs_* files if under pressure.
 */
private const val SQLITE_TEMPORARY_DB = "jdbc:sqlite:"

fun <R> withMemoryDb(block: (conn: Connection) -> R): R {
  val conn = DriverManager.getConnection(SQLITE_MEMORY_DB)

  try {
    return block(conn)
  } finally {
    conn.close()
  }
}

fun <R> withTempDb(block: (conn: Connection) -> R): R {
  val conn = DriverManager.getConnection(SQLITE_TEMPORARY_DB)

  try {
    return block(conn)
  } finally {
    conn.close()
  }
}

/**
Running Gradle under Windows and wanting to use Docker inside WSL2?

Add to `/etc/wsl.conf`:

```ini
[boot]
systemd = true
```

Add to `systemctl edit docker` (after line 2):

```ini
[Service]
ExecStart=
ExecStart=/usr/bin/dockerd -H fd:// -H tcp://0.0.0.0:2375 --tls=false --containerd=/run/containerd/containerd.sock
```

Restart WLS2 (`wsl.exe --shutdown`).

Add Windows Environment Variable: `DOCKER_HOST=tcp://127.0.0.1:2375`. In some cases you will need to replace 127.0.0.1
with the IP of the WSL2 VM (`ip -4 addr show dev eth0`).

Restart Gradle / IDE.
 */
fun <R> withMariadb(block: (conn: Connection) -> R): R {
  MariaDBContainer("mariadb:11.7.2")
    .withCommand(
      "mariadbd",
      "--character_set_server=utf8mb4",
      "--collation_server=utf8mb4_general_ci",
      "--max_allowed_packet=100M"
    )
    .use { ctr ->
      try {
        ctr.start()
        ctr.createConnection("").use { conn ->
          return block(conn)
        }
      } finally {
        ctr.stop()
      }
    }
}

val defaultTestConfig = Config {
  analyticsEnabled = false
  sqlShowAtLogLevel = Level.INFO
}

fun <R> Connection.withMikrator(config: Config = defaultTestConfig, block: (mikrator: Mikrator) -> R): R =
  Mikrator(openDatabase(this), config).use { block(it) }

fun <R> Connection.withResultSet(sql: String, block: (rs: ResultSet) -> R): R =
  createStatement().use { stmt ->
    @Suppress("SqlSourceToSinkFlow")
    stmt.executeQuery(sql).use { rs ->
      block(rs)
    }
  }
