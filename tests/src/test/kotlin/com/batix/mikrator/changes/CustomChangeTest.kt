package com.batix.mikrator.changes

import com.batix.mikrator.dsl.ChangeLog
import com.batix.mikrator.withMemoryDb
import com.batix.mikrator.withMikrator
import liquibase.change.custom.CustomTaskChange
import liquibase.database.Database
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor
import org.junit.jupiter.api.Test

class CustomChangeTest {
  @Test
  fun customChange() {
    withMemoryDb { conn ->
      conn.withMikrator { mikrator ->
        mikrator.update(ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              customChange<TestChange>(mapOf("stringProp" to "something"))
            }
          }
        })
      }
    }
  }

  class TestChange : CustomTaskChange {
    var stringProp: String? = null

    override fun getConfirmationMessage(): String {
      return "custom change ran"
    }

    override fun setUp() {
      println("[setUp] stringProp = $stringProp")
    }

    override fun setFileOpener(resourceAccessor: ResourceAccessor?) {}

    override fun validate(database: Database?): ValidationErrors {
      println("[validate] stringProp = $stringProp")

      return ValidationErrors()
    }

    override fun execute(database: Database?) {
      println("[execute] stringProp = $stringProp")
    }
  }
}
