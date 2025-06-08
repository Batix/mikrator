package com.batix.mikrator.dsl

import com.batix.mikrator.Mikrator
import liquibase.ContextExpression
import liquibase.Labels
import liquibase.change.visitor.AddColumnChangeVisitor
import liquibase.change.visitor.ChangeVisitorFactory
import liquibase.changelog.ChangeSet
import liquibase.changelog.DatabaseChangeLog
import liquibase.database.ObjectQuotingStrategy
import liquibase.parser.core.ParsedNode
import liquibase.precondition.core.PreconditionContainer

@MikratorDsl
public class ChangelogDsl internal constructor(private val cl: DatabaseChangeLog) {
  // see liquibase.changelog.DatabaseChangeLog.handleChildNodeHelper

  /**
   * [ref](https://docs.liquibase.com/concepts/changelogs/changeset.html)
   */
  public fun changeSet(
    id: String,
    author: String,
    comments: String? = null,
    contextFilter: String? = null,
    created: String? = null,
    dbms: String? = null,
    failOnError: Boolean? = null,
    filePath: String = Mikrator.DEFAULT_CHANGESET_PATH, // null throws exception from DB (NOT NULL)
    ignore: Boolean = false,
    labels: String? = null,
    logicalFilePath: String? = null,
    objectQuotingStrategy: ObjectQuotingStrategy? = null,
    onValidationFail: ChangeSet.ValidationFailOption = ChangeSet.ValidationFailOption.HALT,
    runAlways: Boolean = false,
    runInTransaction: Boolean = true,
    runOnChange: Boolean = false,
    runOrder: String? = null,
    runWith: String? = null,
    runWithSpoolFile: String? = null,
    validCheckSums: List<String> = emptyList(),
    block: ChangeSetDsl.() -> Unit,
  ) {
    val cs = ChangeSet(
      id,
      author,
      runAlways,
      runOnChange,
      filePath,
      contextFilter,
      dbms,
      runWith,
      runWithSpoolFile,
      runInTransaction,
      objectQuotingStrategy,
      cl,
    )

    cs.created = created
    cs.failOnError = failOnError
    cs.isIgnore = ignore

    if (labels != null) {
      cs.labels = Labels(labels)
    }

    cs.logicalFilePath = logicalFilePath
    cs.onValidationFail = onValidationFail
    cs.runOrder = runOrder

    cs.comments = comments

    validCheckSums.forEach { sum ->
      cs.addValidCheckSum(sum)
    }

    val dsl = ChangeSetDsl(cs)
    dsl.block()

    cl.addChangeSet(cs)
  }

  /**
   * [ref](https://docs.liquibase.com/concepts/changelogs/preconditions.html)
   */
  public fun preConditions(
    onError: PreconditionContainer.ErrorOption = PreconditionContainer.ErrorOption.HALT,
    onErrorMessage: String? = null,
    onFail: PreconditionContainer.FailOption = PreconditionContainer.FailOption.HALT,
    onFailMessage: String? = null,
    onSqlOutput: PreconditionContainer.OnSqlOutputOption = PreconditionContainer.OnSqlOutputOption.IGNORE,
    block: PreConditionsDsl.() -> Unit,
  ) {
    if (cl.preconditions == null) {
      cl.preconditions = PreconditionContainer()
    }

    cl.preconditions.also { pre ->
      pre.onError = onError
      pre.onErrorMessage = onErrorMessage
      pre.onFail = onFail
      pre.onFailMessage = onFailMessage
      pre.onSqlOutput = onSqlOutput
    }

    val dsl = PreConditionsDsl()
    dsl.block()

    // the root container is an 'and'
    cl.preconditions.nestedPreconditions.addAll(dsl.conditions)
  }

  /**
   * [ref](https://docs.liquibase.com/concepts/changelogs/property-substitution.html)
   */
  public fun property(
    name: String,
    value: String,
    context: String? = null,
    dbms: String? = null,
    global: Boolean = true, // liquibase.changelog.DatabaseChangeLog.handleProperty treats null as true
    labels: String? = null,
  ) {
    cl.changeLogParameters.set(name, value, context, labels, dbms, global, cl)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/remove-change-set-property.html)
   */
  public fun removeChangeSetProperty(
    change: String,
    remove: String,
    dbms: String? = null,
  ) {
    val changeVisitor = ChangeVisitorFactory.getInstance().create(change)

    when (changeVisitor) {
      is AddColumnChangeVisitor -> {
        changeVisitor.load(ParsedNode(null, DatabaseChangeLog.REMOVE_CHANGE_SET_PROPERTY).apply {
          addChild(null, "dbms", dbms)
          addChild(null, "remove", remove)
        }, null)

        cl.changeVisitors.add(changeVisitor)
      }

      null -> { /* ignore */
      }
    }
  }
}

/**
 * [ref](https://docs.liquibase.com/concepts/changelogs/home.html)
 */
public fun ChangeLog(
  contextFilter: String? = null,
  logicalFilePath: String = Mikrator.DEFAULT_CHANGELOG_LOGICAL_PATH,
  objectQuotingStrategy: ObjectQuotingStrategy? = null,
  physicalFilePath: String? = Mikrator.DEFAULT_CHANGELOG_PHYSICAL_PATH,
  block: ChangelogDsl.() -> Unit,
): DatabaseChangeLog {
  val cl = DatabaseChangeLog().also { cl ->
    cl.logicalFilePath = logicalFilePath
    cl.physicalFilePath = physicalFilePath

    if (contextFilter != null) {
      cl.contextFilter = ContextExpression(contextFilter)
    }

    cl.objectQuotingStrategy = objectQuotingStrategy
  }

  ChangelogDsl(cl).block()

  return cl
}
