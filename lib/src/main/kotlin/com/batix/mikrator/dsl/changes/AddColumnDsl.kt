package com.batix.mikrator.dsl.changes

import com.batix.mikrator.dsl.AddColumnColumnDsl
import com.batix.mikrator.dsl.MikratorDsl
import liquibase.change.AddColumnConfig

@MikratorDsl
public class AddColumnDsl internal constructor() {
  internal val columns = mutableListOf<AddColumnConfig>()

  /**
   * [ref](https://docs.liquibase.com/change-types/nested-tags/column.html)
   */
  public fun column(
    name: String,
    type: String,
    afterColumn: String? = null,
    autoIncrement: Boolean? = null,
    beforeColumn: String? = null,
    computed: Boolean? = null,
    generationType: String? = null,
    incrementBy: Number? = null,
    position: Int? = null,
    remarks: String? = null,
    startWith: Number? = null,
    block: AddColumnColumnDsl.() -> Unit = {},
  ) {
    columns.add(AddColumnConfig().also { col ->
      col.name = name
      col.type = type
      col.afterColumn = afterColumn
      col.beforeColumn = beforeColumn
      col.computed = computed
      col.generationType = generationType
      col.incrementBy = incrementBy?.toLong()?.toBigInteger()
      col.isAutoIncrement = autoIncrement
      col.position = position
      col.remarks = remarks
      col.startWith = startWith?.toLong()?.toBigInteger()

      AddColumnColumnDsl(col).apply(block)
    })
  }
}
