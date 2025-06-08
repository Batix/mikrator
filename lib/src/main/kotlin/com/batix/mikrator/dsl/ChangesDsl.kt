package com.batix.mikrator.dsl

import com.batix.mikrator.dsl.changes.*
import liquibase.change.Change
import liquibase.change.ColumnConfig
import liquibase.change.core.*
import liquibase.change.custom.CustomChange
import liquibase.change.custom.CustomChangeWrapper
import liquibase.structure.core.ForeignKeyConstraintType

public open class ChangesDsl internal constructor() {
  internal val changes = mutableListOf<Change>()

  // https://docs.liquibase.com/change-types/home.html

  // -- Entities --

  // - Table -

  /**
   * [ref](https://docs.liquibase.com/change-types/create-table.html)
   */
  public fun createTable(
    tableName: String,
    catalogName: String? = null,
    ifNotExists: Boolean = false,
    remarks: String? = null,
    rowDependencies: Boolean = false,
    schemaName: String? = null,
    tablespace: String? = null,
    tableType: String? = null,
    block: CreateTableDsl.() -> Unit,
  ) {
    val change = CreateTableChange().also { c ->
      c.tableName = tableName
      c.catalogName = catalogName
      c.ifNotExists = ifNotExists
      c.remarks = remarks
      c.rowDependencies = rowDependencies
      c.schemaName = schemaName
      c.tablespace = tablespace
      c.tableType = tableType
    }

    val dsl = CreateTableDsl()
    dsl.block()
    change.columns = dsl.columns

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-table.html)
   */
  public fun dropTable(
    tableName: String,
    cascadeConstraints: Boolean? = null,
    catalogName: String? = null,
    schemaName: String? = null,
  ) {
    val change = DropTableChange().also { c ->
      c.tableName = tableName
      c.isCascadeConstraints = cascadeConstraints
      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/set-table-remarks.html)
   */
  public fun setTableRemarks(
    tableName: String,
    remarks: String,
    catalogName: String? = null,
    schemaName: String? = null,
  ) {
    val change = SetTableRemarksChange().also { c ->
      c.tableName = tableName
      c.remarks = remarks
      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/rename-table.html)
   */
  public fun renameTable(
    oldTableName: String,
    newTableName: String,
    catalogName: String? = null,
    schemaName: String? = null,
  ) {
    val change = RenameTableChange().also { c ->
      c.oldTableName = oldTableName
      c.newTableName = newTableName
      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  // - Column -

  /**
   * [ref](https://docs.liquibase.com/change-types/add-column.html)
   */
  public fun addColumn(
    tableName: String,
    catalogName: String? = null,
    schemaName: String? = null,
    block: AddColumnDsl.() -> Unit,
  ) {
    val change = AddColumnChange().also { c ->
      c.tableName = tableName
      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    val dsl = AddColumnDsl()
    dsl.block()
    change.columns = dsl.columns

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-column.html)
   */
  public fun dropColumn(
    tableName: String,
    vararg columnName: String,
    catalogName: String? = null,
    schemaName: String? = null,
  ) {
    val change = DropColumnChange().also { c ->
      c.tableName = tableName

      if (columnName.size == 1) {
        // only supports single column for SQLite
        c.columnName = columnName[0]
      }
      // it also expects the column in columns
      c.columns = columnName.map { ColumnConfig().apply { name = it } }

      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/rename-column.html)
   */
  public fun renameColumn(
    tableName: String,
    oldColumnName: String,
    newColumnName: String,
    catalogName: String? = null,
    columnDataType: String? = null,
    remarks: String? = null,
    schemaName: String? = null,
  ) {
    val change = RenameColumnChange().also { c ->
      c.tableName = tableName
      c.oldColumnName = oldColumnName
      c.newColumnName = newColumnName
      c.catalogName = catalogName
      c.columnDataType = columnDataType
      c.remarks = remarks
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/modify-data-type.html)
   */
  public fun modifyDataType(
    tableName: String,
    columnName: String,
    newDataType: String,
    catalogName: String? = null,
    schemaName: String? = null,
  ) {
    val change = ModifyDataTypeChange().also { c ->
      c.tableName = tableName
      c.columnName = columnName
      c.newDataType = newDataType
      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/set-column-remarks.html)
   */
  public fun setColumnRemarks(
    tableName: String,
    columnName: String,
    remarks: String,
    catalogName: String? = null,
    columnDataType: String? = null,
    columnParentType: String? = null,
    schemaName: String? = null,
  ) {
    val change = SetColumnRemarksChange().also { c ->
      c.tableName = tableName
      c.columnName = columnName
      c.remarks = remarks
      c.catalogName = catalogName
      c.columnDataType = columnDataType
      c.columnParentType = columnParentType
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/add-auto-increment.html)
   */
  public fun addAutoIncrement(
    tableName: String,
    columnName: String,
    catalogName: String? = null,
    columnDataType: String? = null,
    defaultOnNull: Boolean? = null,
    generationType: String? = null,
    incrementBy: Number? = null,
    schemaName: String? = null,
    startWith: Number? = null,
  ) {
    val change = AddAutoIncrementChange().also { c ->
      c.tableName = tableName
      c.columnName = columnName
      c.catalogName = catalogName
      c.columnDataType = columnDataType
      c.defaultOnNull = defaultOnNull
      c.generationType = generationType
      c.incrementBy = incrementBy?.toLong()?.toBigInteger()
      c.schemaName = schemaName
      c.startWith = startWith?.toLong()?.toBigInteger()
    }

    changes.add(change)
  }

  // - Index -

  /**
   * [ref](https://docs.liquibase.com/change-types/create-index.html)
   */
  public fun createIndex(
    tableName: String,
    associatedWith: String? = null,
    catalogName: String? = null,
    clustered: Boolean? = null,
    indexName: String? = null,
    schemaName: String? = null,
    tablespace: String? = null,
    unique: Boolean? = null,
    using: String? = null,
    block: CreateIndexDsl.() -> Unit,
  ) {
    val change = CreateIndexChange().also { c ->
      c.tableName = tableName
      c.associatedWith = associatedWith
      c.catalogName = catalogName
      c.clustered = clustered
      c.indexName = indexName
      c.schemaName = schemaName
      c.tablespace = tablespace
      c.isUnique = unique
      c.using = using
    }

    val dsl = CreateIndexDsl()
    dsl.block()
    change.columns = dsl.columns

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-index.html)
   */
  public fun dropIndex(
    indexName: String,
    catalogName: String? = null,
    schemaName: String? = null,
    tableName: String? = null,
  ) {
    val change = DropIndexChange().also { c ->
      c.indexName = indexName
      c.catalogName = catalogName
      c.schemaName = schemaName
      c.tableName = tableName
    }

    changes.add(change)
  }

  // - View -

  /**
   * [ref](https://docs.liquibase.com/change-types/create-view.html)
   */
  public fun createView(
    viewName: String,
    selectQuery: String, // normally optional, but we don't support path etc. here
    catalogName: String? = null,
    // encoding (no file support)
    fullDefinition: Boolean? = null,
    // path (no file support)
    // relativeToChangelogFile (no file support)
    remarks: String? = null,
    replaceIfExists: Boolean? = null,
    schemaName: String? = null,
    // tblProperties
  ) {
    val change = CreateViewChange().also { c ->
      c.viewName = viewName
      c.selectQuery = selectQuery
      c.catalogName = catalogName
      c.fullDefinition = fullDefinition
      c.remarks = remarks
      c.replaceIfExists = replaceIfExists
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-view.html)
   */
  public fun dropView(
    viewName: String,
    catalogName: String? = null,
    ifExists: Boolean? = null,
    schemaName: String? = null,
  ) {
    val change = DropViewChange().also { c ->
      c.viewName = viewName
      c.catalogName = catalogName
      c.isIfExists = ifExists
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/rename-view.html)
   */
  public fun renameView(
    oldViewName: String,
    newViewName: String,
    catalogName: String? = null,
    schemaName: String? = null,
  ) {
    val change = RenameViewChange().also { c ->
      c.oldViewName = oldViewName
      c.newViewName = newViewName
      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  // - Procedure -

  public fun createProcedure(
    procedureName: String,
    procedureText: String, // normally optional, but we don't support path etc. here
    catalogName: String? = null,
    // (comments)
    dbms: String? = null,
    // encoding (no file support)
    // path (no file support)
    // relativeToChangelogFile (no file support)
    replaceIfExists: Boolean? = null,
    schemaName: String? = null,
  ) {
    val change = CreateProcedureChange().also { c ->
      c.procedureName = procedureName
      c.procedureText = procedureText
      c.catalogName = catalogName
      c.dbms = dbms
      c.replaceIfExists = replaceIfExists
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-procedure.html)
   */
  public fun dropProcedure(
    procedureName: String,
    catalogName: String? = null,
    schemaName: String? = null,
  ) {
    val change = DropProcedureChange().also { c ->
      c.procedureName = procedureName
      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  // - Sequence -

  /**
   * [ref](https://docs.liquibase.com/change-types/create-sequence.html)
   */
  public fun createSequence(
    sequenceName: String,
    cacheSize: Number? = null,
    catalogName: String? = null,
    cycle: Boolean? = null,
    dataType: String? = null,
    incrementBy: Number? = null,
    maxValue: Number? = null,
    minValue: Number? = null,
    ordered: Boolean? = null,
    schemaName: String? = null,
    startValue: Number? = null,
  ) {
    val change = CreateSequenceChange().also { c ->
      c.sequenceName = sequenceName
      c.cacheSize = cacheSize?.toLong()?.toBigInteger()
      c.catalogName = catalogName
      c.cycle = cycle
      c.dataType = dataType
      c.incrementBy = incrementBy?.toLong()?.toBigInteger()
      c.maxValue = maxValue?.toLong()?.toBigInteger()
      c.minValue = minValue?.toLong()?.toBigInteger()
      c.isOrdered = ordered
      c.schemaName = schemaName
      c.startValue = startValue?.toLong()?.toBigInteger()
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-sequence.html)
   */
  public fun dropSequence(
    sequenceName: String,
    catalogName: String? = null,
    schemaName: String? = null,
  ) {
    val change = DropSequenceChange().also { c ->
      c.sequenceName = sequenceName
      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/rename-sequence.html)
   */
  public fun renameSequence(
    oldSequenceName: String,
    newSequenceName: String,
    catalogName: String? = null,
    schemaName: String? = null,
  ) {
    val change = RenameSequenceChange().also { c ->
      c.oldSequenceName = oldSequenceName
      c.newSequenceName = newSequenceName
      c.catalogName = catalogName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/alter-sequence.html)
   */
  public fun alterSequence(
    sequenceName: String,
    cacheSize: Number? = null,
    catalogName: String? = null,
    cycle: Boolean? = null,
    dataType: String? = null,
    incrementBy: Number? = null,
    maxValue: Number? = null,
    minValue: Number? = null,
    ordered: Boolean? = null,
    schemaName: String? = null,
  ) {
    val change = AlterSequenceChange().also { c ->
      c.sequenceName = sequenceName
      c.cacheSize = cacheSize?.toLong()?.toBigInteger()
      c.catalogName = catalogName
      c.cycle = cycle
      c.dataType = dataType
      c.incrementBy = incrementBy?.toLong()?.toBigInteger()
      c.maxValue = maxValue?.toLong()?.toBigInteger()
      c.minValue = minValue?.toLong()?.toBigInteger()
      c.isOrdered = ordered
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  // -- Constraints --

  /**
   * [ref](https://docs.liquibase.com/change-types/add-default-value.html)
   */
  public fun addDefaultValue(
    tableName: String,
    columnName: String,
    catalogName: String? = null,
    columnDataType: String? = null,
    defaultValueConstraintName: String? = null,
    schemaName: String? = null,
    block: CreateDefaultValueDsl.() -> Unit,
  ) {
    val change = AddDefaultValueChange().also { c ->
      c.tableName = tableName
      c.columnName = columnName
      c.catalogName = catalogName
      c.columnDataType = columnDataType
      c.defaultValueConstraintName = defaultValueConstraintName
      c.schemaName = schemaName
    }

    val dsl = CreateDefaultValueDsl(change)
    dsl.block()

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-default-value.html)
   */
  public fun dropDefaultValue(
    tableName: String,
    columnName: String,
    catalogName: String? = null,
    columnDataType: String? = null,
    schemaName: String? = null,
  ) {
    val change = DropDefaultValueChange().also { c ->
      c.tableName = tableName
      c.columnName = columnName
      c.catalogName = catalogName
      c.columnDataType = columnDataType
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/add-foreign-key-constraint.html)
   */
  public fun addForeignKeyConstraint(
    constraintName: String,
    baseTableName: String,
    baseColumnNames: String,
    referencedTableName: String,
    referencedColumnNames: String,
    baseTableCatalogName: String? = null,
    baseTableSchemaName: String? = null,
    deferrable: Boolean? = null,
    initiallyDeferred: Boolean? = null,
    onDelete: ForeignKeyConstraintType? = null,
    onUpdate: ForeignKeyConstraintType? = null,
    referencedTableCatalogName: String? = null,
    referencedTableSchemaName: String? = null,
    validate: Boolean? = null,
  ) {
    val change = AddForeignKeyConstraintChange().also { c ->
      c.constraintName = constraintName
      c.baseTableName = baseTableName
      c.baseColumnNames = baseColumnNames
      c.referencedTableName = referencedTableName
      c.referencedColumnNames = referencedColumnNames
      c.baseTableCatalogName = baseTableCatalogName
      c.baseTableSchemaName = baseTableSchemaName
      c.deferrable = deferrable
      c.initiallyDeferred = initiallyDeferred
      c.setOnDelete(onDelete)
      c.setOnUpdate(onUpdate)
      c.referencedTableCatalogName = referencedTableCatalogName
      c.referencedTableSchemaName = referencedTableSchemaName
      c.validate = validate
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-foreign-key-constraint.html)
   */
  public fun dropForeignKeyConstraint(
    constraintName: String,
    baseTableName: String,
    baseTableCatalogName: String? = null,
    baseTableSchemaName: String? = null,
  ) {
    val change = DropForeignKeyConstraintChange().also { c ->
      c.constraintName = constraintName
      c.baseTableName = baseTableName
      c.baseTableCatalogName = baseTableCatalogName
      c.baseTableSchemaName = baseTableSchemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-all-foreign-key-constraints.html)
   */
  public fun dropAllForeignKeyConstraints(
    baseTableName: String,
    baseTableCatalogName: String? = null,
    baseTableSchemaName: String? = null,
  ) {
    val change = DropAllForeignKeyConstraintsChange().also { c ->
      c.baseTableName = baseTableName
      c.baseTableCatalogName = baseTableCatalogName
      c.baseTableSchemaName = baseTableSchemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/add-not-null-constraint.html)
   */
  public fun addNotNullConstraint(
    tableName: String,
    columnName: String,
    catalogName: String? = null,
    columnDataType: String? = null,
    constraintName: String? = null,
    defaultNullValue: String? = null,
    schemaName: String? = null,
    validate: Boolean? = null,
  ) {
    val change = AddNotNullConstraintChange().also { c ->
      c.tableName = tableName
      c.columnName = columnName
      c.catalogName = catalogName
      c.columnDataType = columnDataType
      c.constraintName = constraintName
      c.defaultNullValue = defaultNullValue
      c.schemaName = schemaName
      c.validate = validate
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-not-null-constraint.html)
   */
  public fun dropNotNullConstraint(
    tableName: String,
    columnName: String,
    catalogName: String? = null,
    columnDataType: String? = null,
    constraintName: String? = null,
    schemaName: String? = null,
  ) {
    val change = DropNotNullConstraintChange().also { c ->
      c.tableName = tableName
      c.columnName = columnName
      c.catalogName = catalogName
      c.columnDataType = columnDataType
      c.constraintName = constraintName
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/add-primary-key.html)
   */
  public fun addPrimaryKey(
    tableName: String,
    columnNames: String,
    catalogName: String? = null,
    clustered: Boolean? = null,
    constraintName: String? = null,
    forIndexCatalogName: String? = null,
    forIndexName: String? = null,
    forIndexSchemaName: String? = null,
    schemaName: String? = null,
    tablespace: String? = null,
    validate: Boolean? = null,
  ) {
    val change = AddPrimaryKeyChange().also { c ->
      c.tableName = tableName
      c.columnNames = columnNames
      c.catalogName = catalogName
      c.clustered = clustered
      c.constraintName = constraintName
      c.forIndexCatalogName = forIndexCatalogName
      c.forIndexName = forIndexName
      c.forIndexSchemaName = forIndexSchemaName
      c.schemaName = schemaName
      c.tablespace = tablespace
      c.validate = validate
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-primary-key.html)
   */
  public fun dropPrimaryKey(
    tableName: String,
    catalogName: String? = null,
    constraintName: String? = null,
    dropIndex: Boolean? = null,
    schemaName: String? = null,
  ) {
    val change = DropPrimaryKeyChange().also { c ->
      c.tableName = tableName
      c.catalogName = catalogName
      c.constraintName = constraintName
      c.dropIndex = dropIndex
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/add-unique-constraint.html)
   */
  public fun addUniqueConstraint(
    tableName: String,
    columnNames: String,
    catalogName: String? = null,
    clustered: Boolean? = null,
    constraintName: String? = null,
    deferrable: Boolean? = null,
    disabled: Boolean? = null,
    forIndexCatalogName: String? = null,
    forIndexName: String? = null,
    forIndexSchemaName: String? = null,
    initiallyDeferred: Boolean? = null,
    schemaName: String? = null,
    tablespace: String? = null,
    validate: Boolean? = null,
  ) {
    val change = AddUniqueConstraintChange().also { c ->
      c.tableName = tableName
      c.columnNames = columnNames
      c.catalogName = catalogName
      c.clustered = clustered
      c.constraintName = constraintName
      c.deferrable = deferrable
      c.disabled = disabled
      c.forIndexCatalogName = forIndexCatalogName
      c.forIndexName = forIndexName
      c.forIndexSchemaName = forIndexSchemaName
      c.initiallyDeferred = initiallyDeferred
      c.schemaName = schemaName
      c.tablespace = tablespace
      c.validate = validate
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/drop-unique-constraint.html)
   */
  public fun dropUniqueConstraint(
    tableName: String,
    constraintName: String,
    catalogName: String? = null,
    schemaName: String? = null,
    uniqueColumns: String? = null,
  ) {
    val change = DropUniqueConstraintChange().also { c ->
      c.tableName = tableName
      c.constraintName = constraintName
      c.catalogName = catalogName
      c.schemaName = schemaName
      c.uniqueColumns = uniqueColumns
    }

    changes.add(change)
  }

  // -- Data --

  /**
   * [ref](https://docs.liquibase.com/change-types/add-lookup-table.html)
   */
  public fun addLookupTable(
    existingTableName: String,
    existingColumnName: String,
    newTableName: String,
    newColumnName: String,
    constraintName: String? = null,
    existingTableCatalogName: String? = null,
    existingTableSchemaName: String? = null,
    newColumnDataType: String? = null,
    newTableCatalogName: String? = null,
    newTableSchemaName: String? = null,
  ) {
    val change = AddLookupTableChange().also { c ->
      c.existingTableName = existingTableName
      c.existingColumnName = existingColumnName
      c.newTableName = newTableName
      c.newColumnName = newColumnName
      c.constraintName = constraintName
      c.existingTableCatalogName = existingTableCatalogName
      c.existingTableSchemaName = existingTableSchemaName
      c.newColumnDataType = newColumnDataType
      c.newTableCatalogName = newTableCatalogName
      c.newTableSchemaName = newTableSchemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/delete.html)
   */
  public fun delete(
    tableName: String,
    catalogName: String? = null,
    schemaName: String? = null,
    where: String? = null,
    block: UpdateWhereDsl.() -> Unit = {},
  ) {
    val change = DeleteDataChange().also { c ->
      c.tableName = tableName
      c.catalogName = catalogName
      c.schemaName = schemaName
      c.where = where
    }

    val dsl = UpdateWhereDsl()
    dsl.block()
    change.whereParams.addAll(dsl.columns)

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/insert.html)
   */
  public fun insert(
    tableName: String,
    catalogName: String? = null,
    dbms: String? = null,
    schemaName: String? = null,
    block: InsertDsl.() -> Unit,
  ) {
    val change = InsertDataChange().also { c ->
      c.tableName = tableName
      c.catalogName = catalogName
      c.dbms = dbms
      c.schemaName = schemaName
    }

    val dsl = InsertDsl()
    dsl.block()
    change.columns = dsl.columns

    changes.add(change)
  }

  // loadData (no file support)

  // loadUpdateData (no file support)

  /**
   * [ref](https://docs.liquibase.com/change-types/merge-columns.html)
   */
  public fun mergeColumns(
    tableName: String,
    column1Name: String,
    column2Name: String,
    finalColumnName: String,
    finalColumnType: String,
    catalogName: String? = null,
    joinString: String? = null,
    schemaName: String? = null,
  ) {
    val change = MergeColumnChange().also { c ->
      c.tableName = tableName
      c.column1Name = column1Name
      c.column2Name = column2Name
      c.finalColumnName = finalColumnName
      c.finalColumnType = finalColumnType
      c.catalogName = catalogName
      c.joinString = joinString
      c.schemaName = schemaName
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/update.html)
   */
  public fun update(
    tableName: String,
    catalogName: String? = null,
    schemaName: String? = null,
    where: String? = null,
    whereParams: UpdateWhereDsl.() -> Unit = {},
    block: UpdateWhereDsl.() -> Unit,
  ) {
    val change = UpdateDataChange().also { c ->
      c.tableName = tableName
      c.catalogName = catalogName
      c.schemaName = schemaName
      c.where = where
    }

    val whereDsl = UpdateWhereDsl()
    whereDsl.whereParams()
    change.whereParams.addAll(whereDsl.columns)

    val updateDsl = UpdateWhereDsl()
    updateDsl.block()
    change.columns.addAll(updateDsl.columns)

    changes.add(change)
  }

  // -- Miscellaneous --

  /**
   * [ref](https://docs.liquibase.com/change-types/custom-change.html)
   */
  public inline fun <reified T : CustomChange> customChange(
    params: Map<String, String> = emptyMap(),
  ) {
    customChange(T::class.java.name, params)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/custom-change.html)
   */
  public fun customChange(
    className: String,
    params: Map<String, String> = emptyMap(),
  ) {
    val change = CustomChangeWrapper().also { c ->
      c.setClass(className)
    }

    params.forEach { (k, v) ->
      change.setParam(k, v)
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/execute-command.html)
   */
  public fun executeCommand(
    executable: String,
    args: List<String> = emptyList(),
    os: String? = null,
    timeout: String? = null,
  ) {
    val change = ExecuteShellCommandChange().also { c ->
      c.executable = executable
      c.setOs(os)
      c.timeout = timeout
    }

    args.forEach { arg ->
      change.addArg(arg)
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/output.html)
   */
  public fun output(
    message: String,
    target: String? = null,
  ) {
    val change = OutputChange().also { c ->
      c.message = message
      c.target = target
    }

    changes.add(change)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/sql.html)
   */
  public fun sql(
    sql: String,
    dbms: String? = null,
    endDelimiter: String? = null,
    splitStatements: Boolean? = null,
    stripComments: Boolean? = null,
  ) {
    val change = RawSQLChange().also { c ->
      c.sql = sql
      c.dbms = dbms
      c.endDelimiter = endDelimiter
      c.isSplitStatements = splitStatements
      c.isStripComments = stripComments
    }

    changes.add(change)
  }

  // sqlFile (no file support)

  /**
   * [ref](https://docs.liquibase.com/change-types/stop.html)
   */
  public fun stop(message: String? = null) {
    val change = StopChange().also { c ->
      c.message = message
    }

    changes.add(change)
  }

  public fun tagDatabase(
    tag: String,
    keepTagOnRollback: Boolean? = null,
  ) {
    val change = TagDatabaseChange().also { c ->
      c.tag = tag
      c.isKeepTagOnRollback = keepTagOnRollback
    }

    changes.add(change)
  }
}
