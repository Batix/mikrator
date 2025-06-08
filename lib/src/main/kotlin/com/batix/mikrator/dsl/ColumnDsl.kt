package com.batix.mikrator.dsl

import liquibase.change.AddColumnConfig
import liquibase.change.ColumnConfig
import liquibase.change.ConstraintsConfig

@MikratorDsl
public open class CreateTableColumnDsl internal constructor(protected val col: ColumnConfig) {
  public fun default(block: ColumnDefaultValueDsl.() -> Unit) {
    ColumnDefaultValueDsl(col).apply(block)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/nested-tags/column.html#yaml_example:~:text=Constraint%20definitions-,Constraints%20sub%2Dtag,-The%20%3Cconstraints%3E)
   */
  public fun constraints(
    checkConstraint: String? = null,
    deferrable: Boolean? = null,
    deleteCascade: Boolean? = null,
    foreignKeyName: String? = null,
    initiallyDeferred: Boolean? = null,
    notNullConstraintName: String? = null,
    nullable: Boolean? = null,
    primaryKey: Boolean? = null,
    primaryKeyName: String? = null,
    primaryKeyTablespace: String? = null,
    referencedColumnNames: String? = null,
    referencedTableCatalogName: String? = null,
    referencedTableName: String? = null,
    referencedTableSchemaName: String? = null,
    references: String? = null,
    unique: Boolean? = null,
    uniqueConstraintName: String? = null,
    validateForeignKey: Boolean? = null,
    validateNullable: Boolean? = null,
    validatePrimaryKey: Boolean? = null,
    validateUnique: Boolean? = null,
  ) {
    col.constraints = ConstraintsConfig().also { con ->
      con.checkConstraint = checkConstraint
      con.foreignKeyName = foreignKeyName
      con.isDeferrable = deferrable
      con.isDeleteCascade = deleteCascade
      con.isInitiallyDeferred = initiallyDeferred
      con.isNullable = nullable
      con.isPrimaryKey = primaryKey
      con.isUnique = unique
      con.notNullConstraintName = notNullConstraintName
      con.primaryKeyName = primaryKeyName
      con.primaryKeyTablespace = primaryKeyTablespace
      con.referencedColumnNames = referencedColumnNames
      con.referencedTableCatalogName = referencedTableCatalogName
      con.referencedTableName = referencedTableName
      con.referencedTableSchemaName = referencedTableSchemaName
      con.references = references
      con.uniqueConstraintName = uniqueConstraintName
      con.validateForeignKey = validateForeignKey
      con.validateNullable = validateNullable
      con.validatePrimaryKey = validatePrimaryKey
      con.validateUnique = validateUnique
    }
  }
}

@MikratorDsl
public class AddColumnColumnDsl internal constructor(col: AddColumnConfig) : CreateTableColumnDsl(col) {
  public fun value(block: ColumnValueDsl.() -> Unit) {
    ColumnValueDsl(col).apply(block)
  }
}
