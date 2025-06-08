package com.batix.mikrator

import liquibase.database.jvm.JdbcConnection
import java.sql.Connection

/**
 * Unfortunately Liquibase closes the underlying [java.sql.Connection] in [liquibase.database.AbstractJdbcDatabase.close]
 * after restoring the connection's autoCommit setting (which seems weird). We don't want to close a connection that gets
 * passed to us, that would be rude.
 */
internal class NonClosingJdbcConnection(connection: Connection) : JdbcConnection(connection) {
  override fun close() {
    // do nothing
  }
}
