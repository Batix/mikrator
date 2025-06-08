package com.batix.mikrator

import liquibase.report.RollbackReportParameters
import liquibase.report.UpdateReportParameters

public class UpdateTestingRollbackResult(
  public val initialUpdateReport: UpdateReportParameters,
  public val rollbackReport: RollbackReportParameters,
  public val finalUpdateReport: UpdateReportParameters,
)
