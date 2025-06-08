package com.batix.mikrator.dsl

import liquibase.change.ColumnConfig
import liquibase.statement.DatabaseFunction
import liquibase.statement.SequenceNextValueFunction
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@MikratorDsl
public class ColumnDefaultValueDsl internal constructor(private val col: ColumnConfig) {
  public fun stringValue(value: String?) {
    col.defaultValue = value
  }

  public fun numericValue(value: Number?) {
    col.defaultValueNumeric = value
  }

  public fun numericValue(value: String?) {
    col.setDefaultValueNumeric(value)
  }

  public fun booleanValue(value: Boolean?) {
    col.defaultValueBoolean = value
  }

  public fun booleanValue(value: String?) {
    col.setDefaultValueBoolean(value)
  }

  public fun computedValue(value: String, schema: String? = null) {
    col.setDefaultValueComputed(DatabaseFunction(schema, value))
  }

  public fun nextSequenceValue(sequenceName: String, sequenceSchema: String? = null) {
    col.defaultValueSequenceNext = SequenceNextValueFunction(sequenceSchema, sequenceName)
  }

  public fun dateValue(value: Date?) {
    col.defaultValueDate = value
  }

  public fun dateValue(value: String?) {
    col.setDefaultValueDate(value)
  }

  public fun dateValue(value: Instant?) {
    if (value == null) {
      col.setDefaultValueDate(null as Date?)
    } else {
      col.setDefaultValueDate(Date.from(value))
    }
  }

  @OptIn(ExperimentalTime::class)
  public fun dateValue(value: kotlin.time.Instant?) {
    if (value == null) {
      col.setDefaultValueDate(null as Date?)
    } else {
      col.setDefaultValueDate(Date.from(value.toJavaInstant()))
    }
  }
}
