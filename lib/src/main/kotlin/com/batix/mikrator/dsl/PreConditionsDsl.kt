package com.batix.mikrator.dsl

import com.batix.mikrator.Mikrator
import liquibase.database.ObjectQuotingStrategy
import liquibase.precondition.CustomPrecondition
import liquibase.precondition.CustomPreconditionWrapper
import liquibase.precondition.Precondition
import liquibase.precondition.core.*
import kotlin.reflect.KClass

@MikratorDsl
public class PreConditionsDsl internal constructor() {
  internal val conditions = mutableListOf<Precondition>()

  public fun and(block: PreConditionsDsl.() -> Unit) {
    val dsl = PreConditionsDsl()
    dsl.block()

    conditions.add(AndPrecondition().apply {
      nestedPreconditions.addAll(dsl.conditions)
    })
  }

  public fun or(block: PreConditionsDsl.() -> Unit) {
    val dsl = PreConditionsDsl()
    dsl.block()

    conditions.add(OrPrecondition().apply {
      nestedPreconditions.addAll(dsl.conditions)
    })
  }

  public fun not(block: PreConditionsDsl.() -> Unit) {
    val dsl = PreConditionsDsl()
    dsl.block()

    conditions.add(NotPrecondition().apply {
      nestedPreconditions.addAll(dsl.conditions)
    })
  }

  public fun changeLogPropertyDefined(property: String, value: String? = null) {
    conditions.add(ChangeLogPropertyDefinedPrecondition().also { p ->
      p.property = property
      p.value = value
    })
  }

  public fun changeSetExecuted(
    id: String,
    author: String,
    changeLogFile: String = Mikrator.DEFAULT_CHANGELOG_LOGICAL_PATH,
  ) {
    conditions.add(ChangeSetExecutedPrecondition().also { p ->
      p.id = id
      p.author = author
      p.changeLogFile = changeLogFile
    })
  }

  public fun columnExists(
    tableName: String,
    columnName: String,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(ColumnExistsPrecondition().also { p ->
      p.tableName = tableName
      p.columnName = columnName
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun dbms(type: String) {
    conditions.add(DBMSPrecondition().also { p ->
      p.type = type
    })
  }

  public fun expectedQuotingStrategy(strategy: ObjectQuotingStrategy) {
    conditions.add(ObjectQuotingStrategyPrecondition().also { p ->
      p.setStrategy(strategy.name)
    })
  }

  public fun foreignKeyConstraintExists(
    foreignKeyName: String,
    foreignKeyTableName: String,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(ForeignKeyExistsPrecondition().also { p ->
      p.foreignKeyName = foreignKeyName
      p.foreignKeyTableName = foreignKeyTableName
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun indexExists(
    indexName: String? = null,
    tableName: String? = null,
    columnNames: String? = null,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(IndexExistsPrecondition().also { p ->
      p.indexName = indexName
      p.tableName = tableName
      p.columnNames = columnNames
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun primaryKeyExists(
    primaryKeyName: String? = null,
    tableName: String? = null,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(PrimaryKeyExistsPrecondition().also { p ->
      p.primaryKeyName = primaryKeyName
      p.tableName = tableName
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun rowCount(
    expectedRows: Long,
    tableName: String,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(RowCountPrecondition().also { p ->
      p.expectedRows = expectedRows
      p.tableName = tableName
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun runningAs(username: String) {
    conditions.add(RunningAsPrecondition().also { p ->
      p.username = username
    })
  }

  public fun sequenceExists(
    sequenceName: String,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(SequenceExistsPrecondition().also { p ->
      p.sequenceName = sequenceName
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun sqlCheck(
    expectedResult: String,
    sql: String,
  ) {
    conditions.add(SqlPrecondition().also { p ->
      p.expectedResult = expectedResult
      p.sql = sql
    })
  }

  public fun tableExists(
    tableName: String,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(TableExistsPrecondition().also { p ->
      p.tableName = tableName
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun tableIsEmpty(
    tableName: String,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(TableIsEmptyPrecondition().also { p ->
      p.tableName = tableName
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun uniqueConstraintExists(
    tableName: String,
    columnNames: String? = null,
    constraintName: String? = null,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(UniqueConstraintExistsPrecondition().also { p ->
      p.tableName = tableName
      p.columnNames = columnNames
      p.constraintName = constraintName
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun viewExists(
    viewName: String,
    schemaName: String? = null,
    catalogName: String? = null,
  ) {
    conditions.add(ViewExistsPrecondition().also { p ->
      p.viewName = viewName
      p.schemaName = schemaName
      p.catalogName = catalogName
    })
  }

  public fun custom(preconditionClass: KClass<out CustomPrecondition>, params: Map<String, String> = emptyMap()) {
    conditions.add(CustomPreconditionWrapper().also { p ->
      p.className = preconditionClass.qualifiedName
      params.forEach { param ->
        p.setParam(param.key, param.value)
      }
    })
  }
}
