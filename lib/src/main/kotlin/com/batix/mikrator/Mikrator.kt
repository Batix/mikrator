package com.batix.mikrator

import liquibase.*
import liquibase.change.CheckSum
import liquibase.changelog.*
import liquibase.changelog.filter.*
import liquibase.changelog.visitor.ChangeExecListener
import liquibase.changelog.visitor.StatusVisitor
import liquibase.command.CommandScope
import liquibase.command.core.*
import liquibase.command.core.helpers.*
import liquibase.database.Database
import liquibase.diff.DiffGeneratorFactory
import liquibase.diff.DiffResult
import liquibase.diff.compare.CompareControl
import liquibase.diff.output.DiffOutputControl
import liquibase.diff.output.ObjectChangeFilter
import liquibase.lockservice.DatabaseChangeLogLock
import liquibase.report.RollbackReportParameters
import liquibase.report.UpdateReportParameters
import liquibase.sdk.resource.MockResourceAccessor
import liquibase.snapshot.DatabaseSnapshot
import liquibase.snapshot.SnapshotControl
import liquibase.snapshot.SnapshotListener
import liquibase.structure.DatabaseObject
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolutePathString

public class Mikrator(
  private val db: Database,
  private val config: Config = Config(),
) : AutoCloseable {
  override fun close() {
    db.close()
  }

  // -- Update --

  /**
   * [ref](https://docs.liquibase.com/commands/update/update.html)
   */
  public fun update(
    changeLog: DatabaseChangeLog,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
    showSummary: UpdateSummaryEnum? = null,
    showSummaryOutput: UpdateSummaryOutputEnum? = null,
  ): UpdateReportParameters {
    lateinit var updateReport: UpdateReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*UpdateCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(UpdateCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(UpdateCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(UpdateCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY_OUTPUT, showSummaryOutput)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY, showSummary)
      }

      val result = cmd.execute()
      updateReport = result.getResult("updateReport") as UpdateReportParameters
    }

    return updateReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/update/update-count.html)
   */
  public fun updateCount(
    changeLog: DatabaseChangeLog,
    count: Int,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
    showSummary: UpdateSummaryEnum? = null,
    showSummaryOutput: UpdateSummaryOutputEnum? = null,
  ): UpdateReportParameters {
    lateinit var updateReport: UpdateReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*UpdateCountCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(UpdateCountCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(UpdateCountCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(UpdateCountCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(UpdateCountCommandStep.COUNT_ARG, count)
        addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY_OUTPUT, showSummaryOutput)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY, showSummary)
      }

      val result = cmd.execute()
      updateReport = result.getResult("updateReport") as UpdateReportParameters
    }

    return updateReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/update/update-count-sql.html)
   */
  public fun updateCountSql(
    changeLog: DatabaseChangeLog,
    count: Int,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): UpdateReportParameters {
    lateinit var updateReport: UpdateReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*UpdateCountSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(UpdateCountSqlCommandStep.COUNT_ARG, count)
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(UpdateCountSqlCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(UpdateCountSqlCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(UpdateCountSqlCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
      }

      val result = cmd.execute()
      updateReport = result.getResult("updateReport") as UpdateReportParameters
    }

    return updateReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/update/update-sql.html)
   */
  public fun updateSql(
    changeLog: DatabaseChangeLog,
    output: OutputStream,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): UpdateReportParameters {
    lateinit var updateReport: UpdateReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*UpdateSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(UpdateCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(UpdateCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(UpdateCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        setOutput(output)
      }

      val result = cmd.execute()
      updateReport = result.getResult("updateReport") as UpdateReportParameters
    }

    return updateReport
  }


  /**
   * [ref](https://docs.liquibase.com/commands/update/update-testing-rollback.html)
   */
  public fun updateTestingRollback(
    changeLog: DatabaseChangeLog,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
    showSummary: UpdateSummaryEnum? = null,
    showSummaryOutput: UpdateSummaryOutputEnum? = null,
    tag: String? = null,
  ): UpdateTestingRollbackResult {
    // implemented directly, because liquibase.command.core.UpdateTestingRollbackCommandStep.run doesn't keep results

    val changeLogHistoryService =
      Scope.getCurrentScope().getSingleton(ChangeLogHistoryServiceFactory::class.java).getChangeLogService(db)

    val originalChangeSetsCount = changeLogHistoryService.ranChangeSets.size

    val initialUpdateReport = if (tag == null) update(
      changeLog = changeLog,
      changeExecListener = changeExecListener,
      changeLogParameters = changeLogParameters,
      contexts = contexts,
      labelFilter = labelFilter,
      showSummary = showSummary,
      showSummaryOutput = showSummaryOutput,
    ) else updateToTag(
      changeLog = changeLog,
      tag = tag,
      changeExecListener = changeExecListener,
      changeLogParameters = changeLogParameters,
      contexts = contexts,
      labelFilter = labelFilter,
      showSummary = showSummary,
      showSummaryOutput = showSummaryOutput
    )

    changeLogHistoryService.reset()
    val changeSetsDelta = changeLogHistoryService.ranChangeSets.size - originalChangeSetsCount

    val rollbackReport = rollbackCount(
      changeLog = changeLog,
      count = changeSetsDelta,
      changeExecListener = changeExecListener,
      changeLogParameters = changeLogParameters,
      contexts = contexts,
      labelFilter = labelFilter
    )

    val finalUpdateReport = if (tag == null) update(
      changeLog = changeLog,
      changeExecListener = changeExecListener,
      changeLogParameters = changeLogParameters,
      contexts = contexts,
      labelFilter = labelFilter,
      showSummary = showSummary,
      showSummaryOutput = showSummaryOutput,
    ) else updateToTag(
      changeLog = changeLog,
      tag = tag,
      changeExecListener = changeExecListener,
      changeLogParameters = changeLogParameters,
      contexts = contexts,
      labelFilter = labelFilter,
      showSummary = showSummary,
      showSummaryOutput = showSummaryOutput
    )

    return UpdateTestingRollbackResult(initialUpdateReport, rollbackReport, finalUpdateReport)
  }

  /**
   * [ref](https://docs.liquibase.com/commands/update/update-to-tag.html)
   */
  public fun updateToTag(
    changeLog: DatabaseChangeLog,
    tag: String,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
    showSummary: UpdateSummaryEnum? = null,
    showSummaryOutput: UpdateSummaryOutputEnum? = null,
  ): UpdateReportParameters {
    lateinit var updateReport: UpdateReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*UpdateToTagCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(UpdateToTagCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(UpdateToTagCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(UpdateToTagCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(UpdateToTagCommandStep.TAG_ARG, tag)
        addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY_OUTPUT, showSummaryOutput)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        addArgumentValue(ShowSummaryArgument.SHOW_SUMMARY, showSummary)
      }

      val result = cmd.execute()
      updateReport = result.getResult("updateReport") as UpdateReportParameters
    }

    return updateReport
  }

  public fun updateToTagSql(
    changeLog: DatabaseChangeLog,
    tag: String,
    output: OutputStream,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): UpdateReportParameters {
    lateinit var updateReport: UpdateReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*UpdateToTagSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(UpdateToTagSqlCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(UpdateToTagSqlCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(UpdateToTagSqlCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(UpdateToTagCommandStep.TAG_ARG, tag)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        setOutput(output)
      }

      val result = cmd.execute()
      updateReport = result.getResult("updateReport") as UpdateReportParameters
    }

    return updateReport
  }

  // -- Rollback --

  /**
   * [ref](https://docs.liquibase.com/commands/rollback/future-rollback-count-sql.html)
   */
  public fun futureRollbackCountSql(
    changeLog: DatabaseChangeLog,
    count: Int,
    output: OutputStream,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): RollbackReportParameters {
    lateinit var rollbackReport: RollbackReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*FutureRollbackCountSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(FutureRollbackCountSqlCommandStep.COUNT_ARG, count)
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        setOutput(output)
      }

      val result = cmd.execute()
      rollbackReport = result.getResult("rollbackReport") as RollbackReportParameters
    }

    return rollbackReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/rollback/future-rollback-from-tag-sql.html)
   */
  public fun futureRollbackFromTagSql(
    changeLog: DatabaseChangeLog,
    tag: String,
    output: OutputStream,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): RollbackReportParameters {
    lateinit var rollbackReport: RollbackReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*FutureRollbackFromTagSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(FutureRollbackFromTagSqlCommandStep.TAG_ARG, tag)
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        setOutput(output)
      }

      val result = cmd.execute()
      rollbackReport = result.getResult("rollbackReport") as RollbackReportParameters
    }

    return rollbackReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/rollback/future-rollback-sql.html)
   */
  public fun futureRollbackSql(
    changeLog: DatabaseChangeLog,
    output: OutputStream,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): RollbackReportParameters {
    lateinit var rollbackReport: RollbackReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*FutureRollbackSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        setOutput(output)
      }

      val result = cmd.execute()
      rollbackReport = result.getResult("rollbackReport") as RollbackReportParameters
    }

    return rollbackReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/rollback/rollback.html)
   */
  public fun rollback(
    changeLog: DatabaseChangeLog,
    tag: String,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): RollbackReportParameters {
    lateinit var rollbackReport: RollbackReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*RollbackCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(RollbackCommandStep.TAG_ARG, tag)
        // ROLLBACK_SCRIPT_ARG (no file support)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
      }

      val result = cmd.execute()
      rollbackReport = result.getResult("rollbackReport") as RollbackReportParameters
    }

    return rollbackReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/rollback/rollback-count.html)
   */
  public fun rollbackCount(
    changeLog: DatabaseChangeLog,
    count: Int,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): RollbackReportParameters {
    lateinit var rollbackReport: RollbackReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*RollbackCountCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(RollbackCountCommandStep.COUNT_ARG, count)
        // ROLLBACK_SCRIPT_ARG (no file support)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
      }

      val result = cmd.execute()
      rollbackReport = result.getResult("rollbackReport") as RollbackReportParameters
    }

    return rollbackReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/rollback/rollback-count-sql.html)
   */
  public fun rollbackCountSql(
    changeLog: DatabaseChangeLog,
    count: Int,
    output: OutputStream,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): RollbackReportParameters {
    lateinit var rollbackReport: RollbackReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*RollbackCountSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(RollbackCountSqlCommandStep.COUNT_ARG, count)
        // ROLLBACK_SCRIPT_ARG (no file support)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        setOutput(output)
      }

      val result = cmd.execute()
      rollbackReport = result.getResult("rollbackReport") as RollbackReportParameters
    }

    return rollbackReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/rollback/rollback-sql.html)
   */
  public fun rollbackSql(
    changeLog: DatabaseChangeLog,
    tag: String,
    output: OutputStream,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): RollbackReportParameters {
    lateinit var rollbackReport: RollbackReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*RollbackSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(RollbackSqlCommandStep.TAG_ARG, tag)
        // ROLLBACK_SCRIPT_ARG (no file support)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        setOutput(output)
      }

      val result = cmd.execute()
      rollbackReport = result.getResult("rollbackReport") as RollbackReportParameters
    }

    return rollbackReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/rollback/rollback-to-date.html)
   */
  public fun rollbackToDate(
    changeLog: DatabaseChangeLog,
    date: Date,
    output: OutputStream,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): RollbackReportParameters {
    lateinit var rollbackReport: RollbackReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*RollbackToDateSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(RollbackToDateSqlCommandStep.DATE_ARG, date)
        // ROLLBACK_SCRIPT_ARG (no file support)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
        setOutput(output)
      }

      val result = cmd.execute()
      rollbackReport = result.getResult("rollbackReport") as RollbackReportParameters
    }

    return rollbackReport
  }

  /**
   * [ref](https://docs.liquibase.com/commands/rollback/rollback-to-date.html)
   */
  public fun rollbackToDateSql(
    changeLog: DatabaseChangeLog,
    date: Date,
    changeExecListener: ChangeExecListener? = null,
    changeLogParameters: ChangeLogParameters? = null,
    contexts: String? = null,
    labelFilter: String? = null,
  ): RollbackReportParameters {
    lateinit var rollbackReport: RollbackReportParameters

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*RollbackToDateCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_FILE_ARG, "virtual")
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contexts)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labelFilter)
        addArgumentValue(ChangeExecListenerCommandStep.CHANGE_EXEC_LISTENER_ARG, changeExecListener)
        addArgumentValue(RollbackToDateCommandStep.DATE_ARG, date)
        // ROLLBACK_SCRIPT_ARG (no file support)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
      }

      val result = cmd.execute()
      rollbackReport = result.getResult("rollbackReport") as RollbackReportParameters
    }

    return rollbackReport
  }

  // -- Database Inspection --

  /**
   * [ref](https://docs.liquibase.com/commands/inspection/diff.html)
   */
  public fun diff(
    referenceSnapshot: DatabaseSnapshot,
    comparisonSnapshot: DatabaseSnapshot,
    compareControl: CompareControl = CompareControl(),
  ): DiffResult {
    lateinit var diffResult: DiffResult

    Scope.child(config.buildScopeValues()) {
      diffResult = DiffGeneratorFactory.getInstance().compare(referenceSnapshot, comparisonSnapshot, compareControl)
    }

    return diffResult
  }

  /**
   * [ref](https://docs.liquibase.com/commands/inspection/diff.html)
   */
  public fun diff(
    referenceDatabase: Database,
    compareControl: CompareControl = CompareControl(),
  ): DiffResult {
    lateinit var diffResult: DiffResult

    Scope.child(config.buildScopeValues()) {
      diffResult = DiffGeneratorFactory.getInstance().compare(referenceDatabase, db, compareControl)
    }

    return diffResult
  }

  /**
   * [ref](https://docs.liquibase.com/commands/inspection/diff-changelog.html)
   */
  public fun diffChangelog(
    outputFile: Path,
    referenceDatabase: Database,
    snapshotTypes: String? = null,
    schemaComparisons: List<CompareControl.SchemaComparison>? = null,
    objectChangeFilter: ObjectChangeFilter? = null,
    diffOutputControl: DiffOutputControl? = null,
    author: String? = null,
    runOnChangeTypes: String? = null,
    replaceIfExistsTypes: String? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*DiffChangelogCommandStep.COMMAND_NAME).apply {
        // like liquibase.integration.commandline.CommandLineUtils.doDiffToChangeLog
        addArgumentValue(ReferenceDbUrlConnectionCommandStep.REFERENCE_DATABASE_ARG, referenceDatabase)
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(PreCompareCommandStep.SNAPSHOT_TYPES_ARG, DiffCommandStep.parseSnapshotTypes(snapshotTypes))
        addArgumentValue(
          PreCompareCommandStep.COMPARE_CONTROL_ARG,
          CompareControl(schemaComparisons?.toTypedArray(), snapshotTypes)
        )
        addArgumentValue(PreCompareCommandStep.OBJECT_CHANGE_FILTER_ARG, objectChangeFilter)
        addArgumentValue(DiffChangelogCommandStep.CHANGELOG_FILE_ARG, outputFile.absolutePathString())
        addArgumentValue(DiffOutputControlCommandStep.INCLUDE_CATALOG_ARG, diffOutputControl?.includeCatalog)
        addArgumentValue(DiffOutputControlCommandStep.INCLUDE_SCHEMA_ARG, diffOutputControl?.includeSchema)
        addArgumentValue(DiffOutputControlCommandStep.INCLUDE_TABLESPACE_ARG, diffOutputControl?.includeTablespace)
        addArgumentValue(DiffOutputControlCommandStep.EXCLUDE_OBJECTS, diffOutputControl?.excludeObjects)
        addArgumentValue(DiffOutputControlCommandStep.INCLUDE_OBJECTS, diffOutputControl?.includeObjects)
        addArgumentValue(DiffChangelogCommandStep.AUTHOR_ARG, author)
        addArgumentValue(DiffChangelogCommandStep.RUN_ON_CHANGE_TYPES_ARG, runOnChangeTypes)
        addArgumentValue(DiffChangelogCommandStep.REPLACE_IF_EXISTS_TYPES_ARG, replaceIfExistsTypes)

        if (diffOutputControl?.isReplaceIfExistsSet == true) {
          addArgumentValue(GenerateChangelogCommandStep.USE_OR_REPLACE_OPTION, true)
        }
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/inspection/generate-changelog.html)
   */
  public fun generateChangelog(
    outputFile: Path,
    changeLogParameters: ChangeLogParameters? = null,
    compareControl: CompareControl? = null,
    snapshotTypes: List<Class<out DatabaseObject>>? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*GenerateChangelogCommandStep.COMMAND_NAME).apply {
        addArgumentValue(GenerateChangelogCommandStep.CHANGELOG_FILE_ARG, outputFile.absolutePathString())
        addArgumentValue(PreCompareCommandStep.COMPARE_CONTROL_ARG, compareControl)
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(PreCompareCommandStep.SNAPSHOT_TYPES_ARG, snapshotTypes?.toTypedArray())
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_PARAMETERS, changeLogParameters)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/inspection/snapshot.html)
   */
  public fun snapshot(
    schemas: List<CatalogAndSchema>? = null,
    snapshotControl: SnapshotControl? = null,
    snapshotListener: SnapshotListener? = null,
  ): DatabaseSnapshot {
    lateinit var snapshot: DatabaseSnapshot

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*InternalSnapshotCommandStep.COMMAND_NAME).apply {
        addArgumentValue(InternalSnapshotCommandStep.DATABASE_ARG, db)
        addArgumentValue(InternalSnapshotCommandStep.SNAPSHOT_LISTENER_ARG, snapshotListener)
        addArgumentValue(InternalSnapshotCommandStep.SCHEMAS_ARG, schemas?.toTypedArray())
        addArgumentValue(InternalSnapshotCommandStep.SNAPSHOT_CONTROL_ARG, snapshotControl)
      }

      val result = cmd.execute()
      snapshot = result.getResult("snapshot") as DatabaseSnapshot
    }

    return snapshot
  }

  // snapshotReference (same as snapshot, we don't differentiate between Database and ReferenceDatabase)

  // -- Change Tracking --

  /**
   * [ref](https://docs.liquibase.com/commands/change-tracking/history.html)
   */
  public fun history(
    onlyTags: Boolean = false,
    tagsFilter: List<String> = emptyList(),
  ): List<RanChangeSet> {
    // implement ourselves as liquibase.command.core.HistoryCommandStep.DeploymentHistory etc. is not suited
    lateinit var history: List<RanChangeSet>

    Scope.child(config.buildScopeValues()) {
      val historyService = Scope.getCurrentScope()
        .getSingleton(ChangeLogHistoryServiceFactory::class.java)
        .getChangeLogService(db)

      history = historyService.ranChangeSets.filter { cs ->
        when {
          onlyTags && cs.tag.isNullOrEmpty() -> false
          tagsFilter.isNotEmpty() && !tagsFilter.contains(cs.tag) -> false
          else -> true
        }
      }
    }

    return history
  }

  /**
   * [ref](https://docs.liquibase.com/commands/change-tracking/status.html)
   */
  public fun status(
    changeLog: DatabaseChangeLog,
    contextFilter: String? = null,
    labels: String? = null,
  ): List<ChangeSetStatus> {
    val visitor = StatusVisitor(db)

    Scope.child(config.buildScopeValues()) {
      changeLog.validate(db)

      val runtimeEnvironment = RuntimeEnvironment(db, Contexts(contextFilter), LabelExpression(labels))
      changeLogIterator(changeLog).run(visitor, runtimeEnvironment)
    }

    return visitor.changeSetsToRun ?: emptyList()
  }

  /**
   * [ref](https://docs.liquibase.com/commands/change-tracking/unexpected-changesets.html)
   */
  public fun unexpectedChangesets(
    changeLog: DatabaseChangeLog,
    contextFilter: String? = null,
    labels: String? = null,
  ): List<RanChangeSet> {
    lateinit var list: List<RanChangeSet>

    Scope.child(config.buildScopeValues()) {
      list = UnexpectedChangesetsCommandStep.listUnexpectedChangeSets(
        db,
        changeLog,
        Contexts(contextFilter),
        LabelExpression(labels)
      ).toList()
    }

    return list
  }

  // -- Utility --

  /**
   * [ref](https://docs.liquibase.com/commands/utility/calculate-checksum.html)
   */
  public fun calculateChecksum(
    changeLog: DatabaseChangeLog,
    changeSetId: String,
    changeSetAuthor: String,
    changeSetPath: String = DEFAULT_CHANGESET_PATH,
  ): CheckSum {
    // implemented ourselves because liquibase.command.core.CalculateChecksumCommandStep wants to load a file
    lateinit var checksum: CheckSum

    Scope.child(config.buildScopeValues()) {
      val changeSet = changeLog.getChangeSet(changeSetPath, changeSetAuthor, changeSetId)
      check(changeSet != null) { "Couldn't find change set." }

      val changeLogService =
        Scope.getCurrentScope().getSingleton(ChangeLogHistoryServiceFactory::class.java).getChangeLogService(db)
      val ranChangeSet = changeLogService.getRanChangeSet(changeSet)
      val version = ranChangeSet?.lastCheckSum?.let { ChecksumVersion.enumFromChecksumVersion(it.version) }
        ?: ChecksumVersion.latest()

      checksum = changeSet.generateCheckSum(version)
    }

    return checksum
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/changelog-sync.html)
   */
  public fun changelogSync(
    changeLog: DatabaseChangeLog,
    contextFilter: String? = null,
    labels: String? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*ChangelogSyncCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        // CHANGELOG_FILE_ARG
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contextFilter)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labels)
        // CHANGELOG_PARAMETERS
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/changelog-sync-sql.html)
   */
  public fun changelogSyncSql(
    changeLog: DatabaseChangeLog,
    output: OutputStream,
    contextFilter: String? = null,
    labels: String? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*ChangelogSyncSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        // CHANGELOG_FILE_ARG
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contextFilter)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labels)
        // CHANGELOG_PARAMETERS
        setOutput(output)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/changelog-sync-to-tag.html)
   */
  public fun changelogSyncToTag(
    changeLog: DatabaseChangeLog,
    tag: String,
    contextFilter: String? = null,
    labels: String? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*ChangelogSyncToTagCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        // CHANGELOG_FILE_ARG
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contextFilter)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labels)
        // CHANGELOG_PARAMETERS
        addArgumentValue(ChangelogSyncToTagCommandStep.TAG_ARG, tag)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/changelog-sync-to-tag-sql.html)
   */
  public fun changelogSyncToTagSql(
    changeLog: DatabaseChangeLog,
    tag: String,
    output: OutputStream,
    contextFilter: String? = null,
    labels: String? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*ChangelogSyncToTagSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        // CHANGELOG_FILE_ARG
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contextFilter)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labels)
        // CHANGELOG_PARAMETERS
        addArgumentValue(ChangelogSyncToTagSqlCommandStep.TAG_ARG, tag)
        setOutput(output)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/clear-checksums.html)
   */
  public fun clearChecksums() {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*ClearChecksumsCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/db-doc.html)
   */
  public fun dbDoc(
    changeLog: DatabaseChangeLog,
    outputDir: Path,
    contextFilter: String? = null,
    labels: String? = null,
    schemas: List<CatalogAndSchema>? = null,
  ) {
    val accessor = MockResourceAccessor().apply {
      // dbDoc needs the file so it can copy it
      val baos = ByteArrayOutputStream()
      changeLog.serializeTo(baos, FileFormat.YAML)
      setContent(DEFAULT_CHANGELOG_PHYSICAL_PATH, baos.toString())
    }

    Scope.child(config.buildScopeValues() + mapOf(Scope.Attr.resourceAccessor.name to accessor)) {
      val cmd = CommandScope(*DbDocCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        // CHANGELOG_FILE_ARG
        // CHANGELOG_PARAMETERS
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contextFilter)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labels)
        addArgumentValue(DbDocCommandStep.CATALOG_AND_SCHEMAS_ARG, schemas?.toTypedArray())
        addArgumentValue(DbDocCommandStep.OUTPUT_DIRECTORY_ARG, outputDir.absolutePathString())
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/drop-all.html)
   */
  public fun dropAll(
    schemas: List<CatalogAndSchema>? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*DropAllCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DropAllCommandStep.CATALOG_AND_SCHEMAS_ARG, schemas?.toTypedArray())
        // dropDbclhistory is a Pro feature
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/execute-sql.html)
   */
  public fun executeSql(
    sql: String,
    delimiter: String? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*ExecuteSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(ExecuteSqlCommandStep.SQL_ARG, sql)
        addArgumentValue(ExecuteSqlCommandStep.DELIMITER_ARG, delimiter)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/list-locks.html)
   */
  public fun listLocks(): List<DatabaseChangeLogLock> {
    lateinit var locks: List<DatabaseChangeLogLock>

    Scope.child(config.buildScopeValues()) {
      locks = ListLocksCommandStep.listLocks(db).toList()
    }

    return locks
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/mark-next-changeset-ran.html)
   */
  public fun markNextChangeSetRan(
    changeLog: DatabaseChangeLog,
    contextFilter: String? = null,
    labels: String? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*MarkNextChangesetRanCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        // CHANGELOG_FILE_ARG
        // CHANGELOG_PARAMETERS
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contextFilter)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labels)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/mark-next-changeset-ran.html)
   */
  public fun markNextChangeSetRanSql(
    changeLog: DatabaseChangeLog,
    output: OutputStream,
    contextFilter: String? = null,
    labels: String? = null,
  ) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*MarkNextChangesetRanSqlCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        // CHANGELOG_FILE_ARG
        // CHANGELOG_PARAMETERS
        addArgumentValue(DatabaseChangelogCommandStep.CONTEXTS_ARG, contextFilter)
        addArgumentValue(DatabaseChangelogCommandStep.LABEL_FILTER_ARG, labels)
        setOutput(output)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/release-locks.html)
   */
  public fun releaseLocks() {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*ReleaseLocksCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/tag.html)
   */
  public fun tag(tag: String) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*TagCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(TagCommandStep.TAG_ARG, tag)
      }

      cmd.execute()
    }
  }

  /**
   * [ref](https://docs.liquibase.com/commands/utility/tag-exists.html)
   */
  public fun tagExists(tag: String): Boolean {
    var exists = false

    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*TagExistsCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(TagCommandStep.TAG_ARG, tag)
      }

      val result = cmd.execute()
      exists = result.getResult(TagExistsCommandStep.TAG_EXISTS_RESULT)
    }

    return exists
  }

  public fun validate(changeLog: DatabaseChangeLog) {
    Scope.child(config.buildScopeValues()) {
      val cmd = CommandScope(*ValidateCommandStep.COMMAND_NAME).apply {
        addArgumentValue(DbUrlConnectionArgumentsCommandStep.DATABASE_ARG, db)
        addArgumentValue(DatabaseChangelogCommandStep.CHANGELOG_ARG, changeLog)
        // CHANGELOG_FILE_ARG
        // CHANGELOG_PARAMETERS
      }

      cmd.execute()
    }
  }

  // -- helper methods --

  private fun changeLogIterator(
    changeLog: DatabaseChangeLog,
    contexts: Contexts? = null,
    labels: LabelExpression? = null,
  ): ChangeLogIterator {
    return ChangeLogIterator(changeLog, buildList {
      add(ShouldRunChangeSetFilter(db))
      add(ContextChangeSetFilter(contexts))
      add(LabelChangeSetFilter(labels))
      add(DbmsChangeSetFilter(db))
      add(IgnoreChangeSetFilter())
    })
  }

  public companion object {
    public const val DEFAULT_CHANGELOG_PHYSICAL_PATH: String = "virtual" // can't be empty or null for dbDoc
    public const val DEFAULT_CHANGELOG_LOGICAL_PATH: String = "virtual"
    public const val DEFAULT_CHANGESET_PATH: String = ""
  }
}
