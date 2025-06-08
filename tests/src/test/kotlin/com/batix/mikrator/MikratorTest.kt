package com.batix.mikrator

import com.batix.mikrator.dsl.ChangeLog
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.matchers.types.shouldBeInstanceOf
import liquibase.structure.core.Column
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayOutputStream
import java.nio.file.Path
import kotlin.io.path.fileSize

class MikratorTest {
  // -- Update --

  @Test
  fun updateSql() {
    withMemoryDb { conn ->
      val baos = ByteArrayOutputStream()

      conn.withMikrator { mikrator ->
        mikrator.updateSql(ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable("test-table") {
                column("id", "INT")
              }
            }
          }
        }, baos)

        baos.size() shouldBeGreaterThan 0
      }
    }
  }

  @Test
  fun updateTestingRollback() {
    val result = withMemoryDb { conn ->
      conn.withMikrator { mikrator ->
        mikrator.updateTestingRollback(ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable("test-table") {
                column("id", "INT")
              }
            }
          }
        })
      }
    }

    result.initialUpdateReport.success.shouldBeTrue()
    result.rollbackReport.success.shouldBeTrue()
    result.finalUpdateReport.success.shouldBeTrue()
  }

  // -- Rollback --

  @Test
  fun rollback() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"

      val changeLog = ChangeLog {
        changeSet("1-create", "Tester") {
          changes {
            createTable(tblName) {
              column(colId, "INT")
            }
          }
        }

        changeSet("2-insert", "Tester") {
          changes {
            insert(tblName) {
              column(colId) { numericValue(1) }
            }

            tagDatabase("v1")
          }
        }

        changeSet("3-insert", "Tester") {
          changes {
            insert(tblName) {
              column(colId) { numericValue(2) }
            }
          }

          rollback {
            delete(tblName, where = "$colId = 2")
          }
        }
      }

      conn.withMikrator { mikrator ->

        mikrator.update(changeLog)

        conn.withResultSet("SELECT * FROM $tblName") { rs ->
          rs.next() shouldBe true
          rs.getInt(1) shouldBe 1

          rs.next() shouldBe true
          rs.getInt(1) shouldBe 2

          rs.next() shouldBe false
        }

        mikrator.rollback(changeLog, "v1")

        conn.withResultSet("SELECT * FROM $tblName") { rs ->
          rs.next() shouldBe true
          rs.getInt(1) shouldBe 1

          rs.next() shouldBe false
        }
      }
    }
  }

  // -- Database Inspection --

  @Test
  fun diffSnapshots() {
    withMemoryDb { conn ->
      val db = openDatabase(conn)
      val tblName = "tbl"
      val colId = "id"
      val colStr = "str"

      val changeLog = ChangeLog {
        changeSet("1-create", "Tester") {
          changes {
            createTable(tblName) {
              column(colId, "inT")
            }
          }
        }

        changeSet("2-column", "Tester") {
          changes {
            addColumn(tblName) {
              column(colStr, "varChar(23)")
            }
          }
        }
      }

      Mikrator(db, defaultTestConfig).use { mikrator ->
        mikrator.updateCount(changeLog, 1)
        val snapshot1 = mikrator.snapshot()
        mikrator.updateCount(changeLog, 1)
        val snapshot2 = mikrator.snapshot()

        val diffResult = mikrator.diff(snapshot2, snapshot1)

        diffResult.shouldNotBeNull()

        diffResult.missingObjects.should { objs ->
          objs.single().shouldBeInstanceOf<Column>().should { col ->
            col.name shouldBe colStr
          }
        }

        diffResult.unexpectedObjects.shouldBeEmpty()
      }
    }
  }

  @Test
  fun diffDbs() {
    withMemoryDb { conn1 ->
      withMemoryDb { conn2 ->
        val db1 = openDatabase(conn1)
        val db2 = openDatabase(conn2)

        val tblName = "tbl"
        val colId = "id"
        val colStr = "str"

        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "inT")
              }
            }
          }

          changeSet("2-column", "Tester") {
            changes {
              addColumn(tblName) {
                column(colStr, "varChar(23)")
              }
            }
          }
        }

        Mikrator(db1, defaultTestConfig).use { mikrator1 ->
          mikrator1.updateCount(changeLog, 1)
        }

        Mikrator(db2, defaultTestConfig).use { mikrator2 ->
          mikrator2.update(changeLog)
        }

        val diffResult = conn1.withMikrator { mikrator ->
          mikrator.diff(db2)
        }

        diffResult.shouldNotBeNull()

        diffResult.missingObjects.should { objs ->
          objs.single().shouldBeInstanceOf<Column>().should { col ->
            col.name shouldBe colStr
          }
        }

        diffResult.unexpectedObjects.shouldBeEmpty()
      }
    }
  }

  @Test
  fun diffChangelog(@TempDir tempDir: Path) {
    withMemoryDb { conn1 ->
      withMemoryDb { conn2 ->
        val db1 = openDatabase(conn1)
        val db2 = openDatabase(conn2)

        val tblName = "tbl"
        val colId = "id"
        val colStr = "str"

        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "inT")
              }
            }
          }

          changeSet("2-column", "Tester") {
            changes {
              addColumn(tblName) {
                column(colStr, "varChar(23)")
              }
            }
          }
        }

        Mikrator(db1, defaultTestConfig).use { mikrator1 ->
          mikrator1.updateCount(changeLog, 1)
        }

        Mikrator(db2, defaultTestConfig).use { mikrator2 ->
          mikrator2.update(changeLog)
        }

        val changeLogPath = tempDir.resolve("changelog.json")

        conn1.withMikrator { mikrator ->
          mikrator.diffChangelog(changeLogPath, db2)
        }

        changeLogPath.shouldExist()
        changeLogPath.fileSize() shouldBeGreaterThan 0
      }
    }
  }


  @Test
  fun generateChangelog(@TempDir tempDir: Path) {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"
      val colStr = "str"

      val changeLog = ChangeLog {
        changeSet("1-create", "Tester") {
          changes {
            createTable(tblName) {
              column(colId, "inT")
            }
          }
        }

        changeSet("2-column", "Tester") {
          changes {
            addColumn(tblName) {
              column(colStr, "varChar(23)")
            }
          }
        }
      }

      conn.withMikrator { mikrator ->
        mikrator.update(changeLog)

        val changeLogPath = tempDir.resolve("changelog.yaml")
        mikrator.generateChangelog(changeLogPath)

        changeLogPath.shouldExist()
        changeLogPath.fileSize() shouldBeGreaterThan 0
      }
    }
  }

  @Test
  fun snapshot() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"

      conn.withMikrator { mikrator ->
        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "INT")
              }
            }
          }
        }

        mikrator.update(changeLog)

        val snapshot = mikrator.snapshot()

        snapshot.shouldNotBeNull()

        snapshot.serialize(FileFormat.JSON).shouldNotBeEmpty()
        snapshot.serialize(FileFormat.YAML).shouldNotBeEmpty()
      }
    }
  }

  // -- Change Tracking --

  @Test
  fun history() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"
      val colStr = "str"

      conn.withMikrator { mikrator ->
        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "inT")
              }

              tagDatabase("v1")
            }
          }

          changeSet("2-column", "Tester") {
            changes {
              addColumn(tblName) {
                column(colStr, "varCHAR(42)")
              }

              tagDatabase("v2")
            }
          }
        }

        mikrator.update(changeLog)
        val history = mikrator.history(onlyTags = true, tagsFilter = listOf("v1", "v2"))

        history.shouldNotBeEmpty()

        var deploymentId: String? = null

        history.size shouldBe 2

        history[0].should { cs ->
          cs.id shouldBe "1-create"
          deploymentId = cs.deploymentId
        }

        history[1].should { cs ->
          cs.id shouldBe "2-column"
          cs.deploymentId shouldBe deploymentId
        }
      }
    }
  }

  @Test
  fun status() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"
      val colStr = "str"

      val changeLog = ChangeLog {
        changeSet("1-create", "Tester") {
          changes {
            createTable(tblName) {
              column(colId, "inT")
            }
          }
        }

        changeSet("2-column", "Tester") {
          changes {
            addColumn(tblName) {
              column(colStr, "varChar(23)")
            }
          }
        }
      }

      conn.withMikrator { mikrator ->
        mikrator.updateCount(changeLog, 1)
        val status = mikrator.status(changeLog)

        status.single().should { s ->
          s.changeSet.id shouldBe "2-column"
        }
      }
    }
  }

  // -- Utility --

  @Test
  fun calculateChecksum() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"

      val changeLog = ChangeLog {
        changeSet("1-create", "Tester") {
          changes {
            createTable(tblName) {
              column(colId, "inT")
            }
          }
        }
      }

      conn.withMikrator { mikrator ->
        mikrator.update(changeLog)

        val history = mikrator.history()
        val existingChecksum = history.single().lastCheckSum.storedCheckSum

        val checksum = mikrator.calculateChecksum(changeLog, changeSetId = "1-create", changeSetAuthor = "Tester")

        checksum.storedCheckSum shouldBe existingChecksum
      }
    }
  }

  // -- Utility --

  @Test
  fun changelogSyncToTag() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"
      val colStr = "str"

      conn.withMikrator { mikrator ->
        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "inT")
              }

              tagDatabase("v1")
            }
          }

          changeSet("2-column", "Tester") {
            changes {
              addColumn(tblName) {
                column(colStr, "varCHAR(42)")
              }

              tagDatabase("v2")
            }
          }

          changeSet("3-insert", "Tester") {
            changes {
              insert(tblName) {
                column(colId) {
                  numericValue(1)
                }

                column(colStr) {
                  stringValue("test")
                }
              }

              tagDatabase("v3")
            }
          }
        }

        mikrator.updateCount(changeLog, 2)
        mikrator.changelogSyncToTag(changeLog, "v3")

        val history = mikrator.history()

        history.size shouldBe 3
        history[2].id shouldBe "3-insert"

        conn.withResultSet("SELECT * FROM $tblName") { rs ->
          rs.next().shouldBeFalse()
        }
      }
    }
  }

  @Test
  fun dbDoc(@TempDir tempDir: Path) {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"
      val colStr = "str"

      conn.withMikrator { mikrator ->
        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "inT")
              }

              tagDatabase("v1")
            }
          }

          changeSet("2-column", "Tester") {
            changes {
              addColumn(tblName) {
                column(colStr, "varCHAR(42)")
              }

              tagDatabase("v2")
            }
          }

          changeSet("3-insert", "Tester") {
            changes {
              insert(tblName) {
                column(colId) {
                  numericValue(1)
                }

                column(colStr) {
                  stringValue("test")
                }
              }

              tagDatabase("v3")
            }
          }
        }

        mikrator.updateCount(changeLog, 2)
        mikrator.dbDoc(changeLog, tempDir)
      }
    }
  }

  @Test
  fun dropAll() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"

      conn.withMikrator { mikrator ->
        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "INT")
              }
            }
          }
        }

        mikrator.update(changeLog)
        mikrator.dropAll()

        shouldThrowAny {
          conn.withResultSet("SELECT * FROM $tblName") { }
        }
      }
    }
  }

  @Test
  fun executeSql() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"

      conn.withMikrator { mikrator ->
        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "INT")
              }
            }
          }
        }

        mikrator.update(changeLog)
        mikrator.executeSql("DROP TABLE $tblName")


        shouldThrowAny {
          conn.withResultSet("SELECT * FROM $tblName") { }
        }
      }
    }
  }

  @Test
  fun listLocks() {
    withMemoryDb { conn ->
      conn.withMikrator { mikrator ->
        mikrator.listLocks().shouldBeEmpty()
      }
    }
  }

  @Test
  fun markNextChangeSetRan() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"
      val colStr = "str"

      conn.withMikrator { mikrator ->
        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "inT")
              }
            }
          }

          changeSet("2-column", "Tester") {
            changes {
              addColumn(tblName) {
                column(colStr, "varCHAR(42)")
              }
            }
          }
        }

        mikrator.updateCount(changeLog, 1)
        mikrator.markNextChangeSetRan(changeLog)
        mikrator.status(changeLog).shouldBeEmpty()
      }
    }
  }

  @Test
  fun releaseLocks() {
    withMemoryDb { conn ->
      conn.withMikrator { mikrator ->
        mikrator.releaseLocks()
      }
    }
  }

  @Test
  fun tagExists() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"

      conn.withMikrator { mikrator ->
        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "iNt")
              }

              tagDatabase("v1")
            }
          }
        }

        mikrator.update(changeLog)
        mikrator.tagExists("v1").shouldBeTrue()
        mikrator.tagExists("v2").shouldBeFalse()

        mikrator.tag("v2")
        mikrator.tagExists("v1").shouldBeFalse() // gets overwritten
        mikrator.tagExists("v2").shouldBeTrue()
      }
    }
  }

  @Test
  fun validate() {
    withMemoryDb { conn ->
      val tblName = "tbl"
      val colId = "id"
      val colStr = "str"

      conn.withMikrator { mikrator ->
        val changeLog = ChangeLog {
          changeSet("1-create", "Tester") {
            changes {
              createTable(tblName) {
                column(colId, "iNt")
              }
            }
          }

          changeSet("1-create", "Tester") {
            changes {
              addColumn(tblName) {
                column(colStr, "varCHAR(42)")
              }
            }
          }
        }

        shouldThrowAny {
          mikrator.validate(changeLog)
        }
      }
    }
  }
}
