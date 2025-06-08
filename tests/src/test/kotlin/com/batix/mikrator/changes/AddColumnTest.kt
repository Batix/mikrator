package com.batix.mikrator.changes

import com.batix.mikrator.dsl.ChangeLog
import com.batix.mikrator.withMariadb
import com.batix.mikrator.withMikrator
import com.batix.mikrator.withResultSet
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AddColumnTest {
  @Test
  fun addColumn() {
    // no default value on addColumn with SQLite, why?
    withMariadb { conn ->
      val tableName = "tbl"
      val idColumn = "id"
      val strColumn = "str"
      val dateColumn = "d"

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
                  default {
                    stringValue("str-default")
                  }
                }
              }

              // id | str
              // 1  | str-default
              // 2  | str-custom
              insert(tableName) {
                column(idColumn) {
                  numericValue(2)
                }

                column(strColumn) {
                  stringValue("str-custom")
                }
              }

              // id | str         | d
              // 1  | str-default | 2008-08-08
              // 2  | str-custom  | 2008-08-08
              addColumn(tableName) {
                column(dateColumn, "Date") {
                  value {
                    dateValue("2008-08-08")
                  }
                }
              }
            }
          }
        })

        conn.withResultSet("SELECT * FROM $tableName ORDER BY $idColumn") { rs ->
          rs.next() shouldBe true
          rs.getInt(1) shouldBe 1
          rs.getString(2) shouldBe "str-default"
          rs.getDate(3) shouldBe java.sql.Date.valueOf("2008-08-08")

          rs.next() shouldBe true
          rs.getInt(1) shouldBe 2
          rs.getString(2) shouldBe "str-custom"
          rs.getDate(3) shouldBe java.sql.Date.valueOf("2008-08-08")

          rs.next() shouldBe false
        }
      }
    }
  }
}
