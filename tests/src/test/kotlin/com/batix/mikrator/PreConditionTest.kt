package com.batix.mikrator

import com.batix.mikrator.dsl.ChangeLog
import io.kotest.assertions.throwables.shouldThrowAny
import liquibase.precondition.core.PreconditionContainer
import org.junit.jupiter.api.Test

class PreConditionTest {
  @Test
  fun failMarkRan() {
    withMemoryDb { conn ->
      conn.withMikrator { mikrator ->
        mikrator.update(ChangeLog {
          changeSet("1-create", "Tester") {
            preConditions(onFail = PreconditionContainer.FailOption.MARK_RAN) {
              tableExists("test-table")
            }

            changes {
              createTable("test-table") {
                column("id", "INT")
              }
            }
          }
        })

        println(shouldThrowAny {
          conn.withResultSet("SELECT * FROM `test-table`") { }
        })
      }
    }
  }
}
