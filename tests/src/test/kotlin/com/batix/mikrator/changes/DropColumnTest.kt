package com.batix.mikrator.changes

import com.batix.mikrator.dsl.ChangeLog
import com.batix.mikrator.withMemoryDb
import com.batix.mikrator.withMikrator
import com.batix.mikrator.withResultSet
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DropColumnTest {
  @Test
  fun dropColumn() {
    withMemoryDb { conn ->
      val tableName = "tbl"
      val idColumn = "id"
      val strColumn = "str"

      conn.withMikrator { mikrator ->
        mikrator.update(ChangeLog {
          changeSet("1", "Tester") {
            changes {
              // id
              createTable(tableName) {
                column(idColumn, "INT")
              }

              // id
              // 1
              insert(tableName) {
                column(idColumn) {
                  numericValue(1)
                }
              }

              // id | str
              // 1  | str-default
              addColumn(tableName) {
                column(strColumn, "varchar(100)") {
                  value {
                    stringValue("str-default")
                  }
                }
              }

              // id
              // 1
              dropColumn(tableName, strColumn)
            }
          }
        })

        conn.withResultSet("SELECT * FROM $tableName ORDER BY $idColumn") { rs ->
          rs.next() shouldBe true
          rs.metaData.columnCount shouldBe 1
          rs.getInt(1) shouldBe 1

          rs.next() shouldBe false
        }
      }
    }
  }
}
