package com.batix.mikrator.dsl

import liquibase.changelog.ChangeSet
import liquibase.precondition.core.PreconditionContainer

@MikratorDsl
public class ChangeSetDsl internal constructor(private val cs: ChangeSet) {
  /**
   * [ref](https://docs.liquibase.com/concepts/changelogs/preconditions.html)
   */
  public fun preConditions(
    onError: PreconditionContainer.ErrorOption = PreconditionContainer.ErrorOption.HALT,
    onErrorMessage: String? = null,
    onFail: PreconditionContainer.FailOption = PreconditionContainer.FailOption.HALT,
    onFailMessage: String? = null,
    onSqlOutput: PreconditionContainer.OnSqlOutputOption = PreconditionContainer.OnSqlOutputOption.IGNORE,
    block: PreConditionsDsl.() -> Unit,
  ) {
    if (cs.preconditions == null) {
      cs.preconditions = PreconditionContainer()
    }

    cs.preconditions.also { pre ->
      pre.onError = onError
      pre.onErrorMessage = onErrorMessage
      pre.onFail = onFail
      pre.onFailMessage = onFailMessage
      pre.onSqlOutput = onSqlOutput
    }

    val dsl = PreConditionsDsl()
    dsl.block()

    // the root container is an 'and'
    cs.preconditions.nestedPreconditions.addAll(dsl.conditions)
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/home.html)
   */
  public fun changes(block: ChangesDsl.() -> Unit) {
    val dsl = ChangesDsl()
    dsl.block()

    dsl.changes.forEach { cs.addChange(it) }
  }

  /**
   * [ref](https://docs.liquibase.com/change-types/home.html)
   */
  public fun rollback(block: ChangesDsl.() -> Unit) {
    val dsl = ChangesDsl()
    dsl.block()

    dsl.changes.forEach { cs.addRollbackChange(it) }
  }
}
