package com.batix.mikrator.dsl.changes

import com.batix.mikrator.dsl.CreateTableColumnDsl
import com.batix.mikrator.dsl.MikratorDsl
import liquibase.change.ColumnConfig

@MikratorDsl
public class CreateTableDsl internal constructor() {
  internal val columns = mutableListOf<ColumnConfig>()

  /**
   * [ref](https://docs.liquibase.com/change-types/nested-tags/column.html)
   */
  public fun column(
    name: String,
    type: String,
    autoIncrement: Boolean? = null,
    computed: Boolean? = null,
    generationType: String? = null,
    incrementBy: Number? = null,
    remarks: String? = null,
    startWith: Number? = null,
    block: CreateTableColumnDsl.() -> Unit = {},
  ) {
    columns.add(ColumnConfig().also { col ->
      col.name = name
      col.type = type
      col.isAutoIncrement = autoIncrement
      col.computed = computed
      col.generationType = generationType
      col.incrementBy = incrementBy?.toLong()?.toBigInteger()
      col.remarks = remarks
      col.startWith = startWith?.toLong()?.toBigInteger()

      CreateTableColumnDsl(col).apply(block)
    })
  }
}
