package com.batix.mikrator.changes

import com.batix.mikrator.dsl.ChangeLog
import com.batix.mikrator.withMemoryDb
import com.batix.mikrator.withMikrator
import com.batix.mikrator.withResultSet
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DeleteTest {
  @Test
  fun delete() {
    withMemoryDb { conn ->
      val tableName = "tbl"
      val idCol = "id"

      conn.withMikrator { mikrator ->
        mikrator.update(ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tableName) {
                column(idCol, "INT") {
                  constraints(nullable = false)
                }
              }
            }
          }

          changeSet("2-insert", "Tester") {
            changes {
              insert(tableName) {
                column(idCol) { numericValue(3) }
              }

              insert(tableName) {
                column(idCol) { numericValue(1) }
              }

              insert(tableName) {
                column(idCol) { numericValue(4) }
              }
            }
          }

          changeSet("3-delete", "Tester") {
            changes {
              delete(tableName, where = ":name = :value") {
                column(idCol) { numericValue(1) }
              }
            }
          }
        })

        conn.withResultSet("SELECT * FROM $tableName") { rs ->
          rs.next() shouldBe true
          rs.getInt(1) shouldBe 3

          rs.next() shouldBe true
          rs.getInt(1) shouldBe 4

          rs.next() shouldBe false
        }
      }
    }
  }
}
