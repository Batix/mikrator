package com.batix.mikrator.dsl

import liquibase.change.ColumnConfig
import liquibase.statement.DatabaseFunction
import liquibase.statement.SequenceCurrentValueFunction
import liquibase.statement.SequenceNextValueFunction
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@MikratorDsl
public class ColumnValueDsl internal constructor(private val col: ColumnConfig) {
  public fun stringValue(value: String?) {
    col.value = value
  }

  public fun numericValue(value: Number?) {
    col.valueNumeric = value
  }

  public fun numericValue(value: String?) {
    col.setValueNumeric(value)
  }

  public fun booleanValue(value: Boolean?) {
    col.valueBoolean = value
  }

  public fun booleanValue(value: String?) {
    col.setValueBoolean(value)
  }

  public fun computedValue(value: String, schema: String? = null) {
    col.setValueComputed(DatabaseFunction(schema, value))
  }

  public fun nextSequenceValue(sequenceName: String, sequenceSchema: String? = null) {
    col.valueSequenceNext = SequenceNextValueFunction(sequenceSchema, sequenceName)
  }

  public fun currentSequenceValue(sequenceName: String, sequenceSchema: String? = null) {
    col.valueSequenceCurrent = SequenceCurrentValueFunction(sequenceSchema, sequenceName)
  }

  public fun dateValue(value: Date?) {
    col.valueDate = value
  }

  public fun dateValue(value: String?) {
    col.setValueDate(value)
  }

  public fun dateValue(value: java.time.Instant?) {
    if (value == null) {
      col.setValueDate(null as Date?)
    } else {
      col.setValueDate(Date.from(value))
    }
  }

  @OptIn(ExperimentalTime::class)
  public fun dateValue(value: kotlin.time.Instant?) {
    if (value == null) {
      col.setValueDate(null as Date?)
    } else {
      col.setValueDate(Date.from(value.toJavaInstant()))
    }
  }

  public fun blobFileValue(fileName: String?) {
    col.valueBlobFile = fileName
  }

  public fun clobFileValue(fileName: String?, encoding: String? = null) {
    col.valueClobFile = fileName
    col.encoding = encoding
  }
}
