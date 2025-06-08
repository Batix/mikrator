package com.batix.mikrator.changes

import com.batix.mikrator.dsl.ChangeLog
import com.batix.mikrator.withMemoryDb
import com.batix.mikrator.withMikrator
import com.batix.mikrator.withResultSet
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CreateTableTest {
  @Test
  fun create() {
    withMemoryDb { conn ->
      conn.withMikrator { mikrator ->
        mikrator.update(ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable("test-table") {
                column("id", "INT")
              }
            }
          }
        })

        conn.withResultSet("SELECT * FROM `test-table`") { rs ->
          rs.next() shouldBe false
        }
      }
    }
  }
}
