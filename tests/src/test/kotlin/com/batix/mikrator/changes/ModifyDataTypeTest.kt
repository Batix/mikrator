package com.batix.mikrator.changes

import com.batix.mikrator.dsl.ChangeLog
import com.batix.mikrator.withMariadb
import com.batix.mikrator.withMikrator
import com.batix.mikrator.withResultSet
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test

class ModifyDataTypeTest {
  @Test
  fun modifyDataType() {
    // "modifyDataType is not supported on sqlite"
    withMariadb { conn ->
      val tableName = "tbl"
      val idColumn = "id"
      val strColumn = "str"

      conn.withMikrator { mikrator ->
        mikrator.update(ChangeLog {
          changeSet("1", "Tester") {
            changes {
              // id | str
              createTable(tableName) {
                column(idColumn, "INT")
                column(strColumn, "varchar(100)")
              }

              // id | str
              // 1  | 5
              insert(tableName) {
                column(idColumn) {
                  numericValue(1)
                }

                column(strColumn) {
                  stringValue("5")
                }
              }

              modifyDataType(tableName, strColumn, "Int")
              modifyDataType(tableName, idColumn, "Varchar(100)")
            }
          }
        })

        conn.withResultSet("SELECT * FROM $tableName ORDER BY $idColumn") { rs ->
          rs.next() shouldBe true
          rs.getObject(1).shouldBeInstanceOf<String>()
          rs.getString(1) shouldBe "1"
          rs.getObject(2).shouldBeInstanceOf<Int>()
          rs.getInt(2) shouldBe 5

          rs.next() shouldBe false
        }
      }
    }
  }
}
