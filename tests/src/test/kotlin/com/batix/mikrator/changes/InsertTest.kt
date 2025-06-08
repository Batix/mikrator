package com.batix.mikrator.changes

import com.batix.mikrator.dsl.ChangeLog
import com.batix.mikrator.withMemoryDb
import com.batix.mikrator.withMikrator
import com.batix.mikrator.withResultSet
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class InsertTest {
  @Test
  fun insert() {
    withMemoryDb { conn ->
      conn.withMikrator { mikrator ->
        mikrator.update(ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable("test-table") {
                column("id", "INT") {
                  constraints(nullable = false)
                }
                column("t", "datetime") {
                  default {
                    computedValue("(datetime())")
                  }
                }
              }
            }
          }

          changeSet("2-insert", "Tester") {
            changes {
              insert("test-table") {
                column("id") { numericValue(5) }
              }
            }
          }
        })

        conn.withResultSet("SELECT * FROM `test-table`") { rs ->
          rs.next() shouldBe true
          rs.getInt(1) shouldBe 5
          rs.getTimestamp(2).shouldNotBeNull()
          rs.next() shouldBe false
        }
      }
    }
  }
}
