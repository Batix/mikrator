package com.batix.mikrator.dsl.changes

import com.batix.mikrator.dsl.ColumnValueDsl
import com.batix.mikrator.dsl.MikratorDsl
import liquibase.change.ColumnConfig

@MikratorDsl
public class InsertDsl internal constructor() {
  internal val columns = mutableListOf<ColumnConfig>()

  /**
   * [ref](https://docs.liquibase.com/change-types/nested-tags/column.html)
   */
  public fun column(
    name: String,
    block: ColumnValueDsl.() -> Unit,
  ) {
    columns.add(ColumnConfig().also { col ->
      col.name = name

      ColumnValueDsl(col).apply(block)
    })
  }
}
