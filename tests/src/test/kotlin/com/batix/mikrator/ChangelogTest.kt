package com.batix.mikrator

import com.batix.mikrator.dsl.ChangeLog
import org.junit.jupiter.api.Test

class ChangelogTest {
  @Test
  fun removeChangeSetProperty() {
    withMemoryDb { conn ->
      conn.withMikrator { mikrator ->
        mikrator.update(ChangeLog {
          removeChangeSetProperty("addColumn", "beforeColumn")

          changeSet("1-create", "Tester") {
            changes {
              createTable("tbl") {
                column("id", "INT")
              }
            }
          }

          changeSet("2-add", "Tester") {
            changes {
              addColumn("tbl") {
                column("str", "varchar(100)", beforeColumn = "id")
              }
            }
          }
        })
      }
    }
  }
}
