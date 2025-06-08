package com.batix.mikrator.changes

import com.batix.mikrator.dsl.ChangeLog
import com.batix.mikrator.withMemoryDb
import com.batix.mikrator.withMikrator
import com.batix.mikrator.withResultSet
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RenameColumnTest {
  @Test
  fun renameColumn() {
    withMemoryDb { conn ->
      val tableName = "tbl"
      val idColumn = "id"
      val strColumn = "str"
      val strType = "varchar(100)"
      val str2Column = "str2"

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
                column(strColumn, strType) {
                  value {
                    stringValue("str-default")
                  }
                }
              }

              // id | str2
              // 1  | str-default
              renameColumn(tableName, strColumn, str2Column, columnDataType = strType)
            }
          }
        })

        conn.withResultSet("SELECT * FROM $tableName ORDER BY $idColumn") { rs ->
          rs.next() shouldBe true
          rs.getInt(1) shouldBe 1
          rs.getString(str2Column) shouldBe "str-default"

          rs.next() shouldBe false
        }
      }
    }
  }
}
