package com.batix.mikrator.dsl.changes

import com.batix.mikrator.dsl.MikratorDsl
import liquibase.change.AddColumnConfig

@MikratorDsl
public class CreateIndexDsl internal constructor() {
  internal val columns = mutableListOf<AddColumnConfig>()

  /**
   * [ref](https://docs.liquibase.com/change-types/nested-tags/column.html)
   */
  public fun column(
    name: String,
    computed: Boolean? = null,
    descending: Boolean? = null,
  ) {
    columns.add(AddColumnConfig().also { col ->
      col.name = name
      col.computed = computed
      col.descending = descending
    })
  }
}
