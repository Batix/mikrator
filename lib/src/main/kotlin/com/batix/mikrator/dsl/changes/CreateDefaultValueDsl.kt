package com.batix.mikrator.dsl.changes

import com.batix.mikrator.dsl.MikratorDsl
import liquibase.change.core.AddDefaultValueChange
import liquibase.statement.DatabaseFunction
import liquibase.statement.SequenceNextValueFunction
import liquibase.util.ISODateFormat
import org.apache.commons.lang3.StringUtils
import java.text.NumberFormat
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant

@MikratorDsl
public class CreateDefaultValueDsl internal constructor(private val change: AddDefaultValueChange) {
  /*
  AddDefaultValueChange only has String setters for some things, so convert to String in the format it expects for
  non-Strings. Vice versa for the ones without String setters.
   */

  public fun stringValue(value: String?) {
    change.defaultValue = value
  }

  public fun numericValue(value: Number?) {
    change.defaultValueNumeric = value?.let { n ->
      NumberFormat.getInstance(Locale.US).format(n)
    }
  }

  public fun numericValue(value: String?) {
    change.defaultValueNumeric = value
  }

  public fun booleanValue(value: Boolean?) {
    change.defaultValueBoolean = value
  }

  public fun booleanValue(value: String?) {
    val v = StringUtils.trimToNull(value?.lowercase())

    change.defaultValueBoolean = when (v) {
      "true", "1" -> true
      "false", "0" -> false
      "", null -> null
      else -> {
        computedValue(v)
        null
      }
    }
  }

  public fun computedValue(value: String, schema: String? = null) {
    change.defaultValueComputed = DatabaseFunction(schema, value)
  }

  public fun nextSequenceValue(sequenceName: String, sequenceSchema: String? = null) {
    change.defaultValueSequenceNext = SequenceNextValueFunction(sequenceSchema, sequenceName)
  }

  public fun dateValue(value: Date?) {
    change.defaultValueDate = ISODateFormat().format(value)
  }

  public fun dateValue(value: String?) {
    change.defaultValueDate = value
  }

  public fun dateValue(value: Instant?) {
    if (value == null) {
      change.defaultValueDate = null as String?
    } else {
      dateValue(Date.from(value))
    }
  }

  @OptIn(ExperimentalTime::class)
  public fun dateValue(value: kotlin.time.Instant?) {
    if (value == null) {
      change.defaultValueDate = null as String?
    } else {
      dateValue(Date.from(value.toJavaInstant()))
    }
  }
}
