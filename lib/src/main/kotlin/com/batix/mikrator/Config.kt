package com.batix.mikrator

import com.batix.mikrator.dsl.MikratorDsl
import liquibase.GlobalConfiguration
import liquibase.Scope
import liquibase.SupportsMethodValidationLevelsEnum
import liquibase.analytics.AnalyticsListener
import liquibase.analytics.NoOpAnalyticsListener
import liquibase.analytics.configuration.AnalyticsArgs
import liquibase.analytics.configuration.AnalyticsConfiguration
import liquibase.configuration.ConfigurationDefinition
import liquibase.license.LicenseTrackingArgs
import liquibase.resource.ResourceAccessor
import liquibase.sql.SqlConfiguration
import liquibase.ui.UIServiceEnum
import java.nio.charset.Charset
import java.util.logging.Level

@MikratorDsl
public class Config {
  private val additional: MutableMap<String, Any> = mutableMapOf()

  public operator fun <T : Any> set(configuration: ConfigurationDefinition<T>, value: T) {
    additional[configuration.key] = value
  }

  // -- Scope.Attr --

  public var resourceAccessor: ResourceAccessor = EmptyResourceAccessor()

  // see inheritors of liquibase.configuration.AutoloadedConfigurations

  // -- GlobalConfiguration --

  public var allowDuplicatedChangesetIdentifiers: Boolean? = null
  public var alwaysDropInsteadOfReplace: Boolean? = null
  public var alwaysOverrideStoredLogicSchema: Boolean? = null
  public var autoReorg: Boolean? = null
  public var changeLogLockPollRate: Long? = null
  public var changeLogLockWaitTime: Long? = null
  public var convertDataTypes: Boolean? = null
  public var databaseChangeLogTableName: String? = null
  public var databaseChangeLogLockTableName: String? = null
  public var ddlLockTimeout: Int? = null
  public var diffColumnOrder: Boolean? = null
  public var duplicateFileMode: GlobalConfiguration.DuplicateFileMode? = null
  public var fileEncoding: Charset? = null
  public var generateChangeSetCreatedValues: Boolean? = null
  public var generatedChangeSetIdsIncludeDescription: Boolean? = null
  public var headless: Boolean? = null
  public var includeCatalogInSpecification: Boolean? = null
  public var includeRelationsForComputedColumns: Boolean? = null
  public var includeSchemaNameForDefault: Boolean? = null
  public var liquibaseCatalogName: String? = null
  public var liquibaseSchemaName: String? = null
  public var liquibaseTablespaceName: String? = null
  public var outputFileEncoding: String? = null
  public var outputLineSeparator: String? = null
  public var preserveClasspathPrefixInNormalizedPaths: Boolean? = null
  public var preserveSchemaCase: Boolean? = null
  public var searchPath: String? = null
  public var secureParsing: Boolean? = null
  public var shouldSnapshotData: Boolean? = null
  public var showBanner: Boolean? = null
  public var strict: Boolean? = null
  public var supportsMethodValidationLevel: SupportsMethodValidationLevelsEnum? = null
  public var trimLoadDataFileHeader: Boolean? = null
  public var uiService: UIServiceEnum? = null
  public var validateXmlChangeLogFiles: Boolean? = null

  // -- AnalyticsArgs --

  public var analyticsConfigCacheTimeoutMillis: Long? = null
  public var analyticsConfigEndpointTimeoutMillis: Int? = null
  public var analyticsConfigEndpointUrl: String? = null
  public var analyticsDevOverride: Boolean? = null
  public var analyticsEnabled: Boolean? = null
  public var analyticsLicenseKeyChars: Int? = null
  public var analyticsLogLevel: Level? = null
  public var analyticsTimeoutMillis: Int? = null

  // -- LicenseTrackingArgs --

  public var licenseTrackingEnabled: Boolean? = null
  public var licenseTrackingLogLevel: Level? = null
  public var licenseTrackingTimeout: Int? = null
  public var licenseTrackingTrackingId: String? = null
  public var licenseTrackingUrl: String? = null

  // -- SqlConfiguration --

  public var sqlAlwaysSetFetchSize: Boolean? = null
  public var sqlShowAtLogLevel: Level? = null
  public var sqlShowSqlWarningMessages: Boolean? = null

  // ignore LiquibaseCommandLineConfiguration

  // ignore ChangeLogParserConfiguration

  internal fun buildScopeValues() = buildMap {
    fun <T> conf(a: Scope.Attr, v: T?) {
      if (v != null) {
        put(a.name, v)
      }
    }

    fun <T> conf(c: ConfigurationDefinition<T>, v: T?) {
      if (v != null) {
        put(c.key, v)
      }
    }

    conf(Scope.Attr.resourceAccessor, resourceAccessor)

    conf(GlobalConfiguration.ALLOW_DUPLICATED_CHANGESETS_IDENTIFIERS, allowDuplicatedChangesetIdentifiers)
    conf(GlobalConfiguration.ALWAYS_DROP_INSTEAD_OF_REPLACE, alwaysDropInsteadOfReplace)
    conf(GlobalConfiguration.ALWAYS_OVERRIDE_STORED_LOGIC_SCHEMA, alwaysOverrideStoredLogicSchema)
    conf(GlobalConfiguration.AUTO_REORG, autoReorg)
    conf(GlobalConfiguration.CHANGELOGLOCK_POLL_RATE, changeLogLockPollRate)
    conf(GlobalConfiguration.CHANGELOGLOCK_WAIT_TIME, changeLogLockWaitTime)
    conf(GlobalConfiguration.CONVERT_DATA_TYPES, convertDataTypes)
    conf(GlobalConfiguration.DATABASECHANGELOG_TABLE_NAME, databaseChangeLogTableName)
    conf(GlobalConfiguration.DATABASECHANGELOGLOCK_TABLE_NAME, databaseChangeLogLockTableName)
    conf(GlobalConfiguration.DDL_LOCK_TIMEOUT, ddlLockTimeout)
    conf(GlobalConfiguration.DIFF_COLUMN_ORDER, diffColumnOrder)
    conf(GlobalConfiguration.DUPLICATE_FILE_MODE, duplicateFileMode)
    conf(GlobalConfiguration.FILE_ENCODING, fileEncoding)
    conf(GlobalConfiguration.GENERATE_CHANGESET_CREATED_VALUES, generateChangeSetCreatedValues)
    conf(GlobalConfiguration.GENERATED_CHANGESET_IDS_INCLUDE_DESCRIPTION, generatedChangeSetIdsIncludeDescription)
    conf(GlobalConfiguration.HEADLESS, headless)
    conf(GlobalConfiguration.INCLUDE_CATALOG_IN_SPECIFICATION, includeCatalogInSpecification)
    conf(GlobalConfiguration.INCLUDE_RELATIONS_FOR_COMPUTED_COLUMNS, includeRelationsForComputedColumns)
    conf(GlobalConfiguration.INCLUDE_SCHEMA_NAME_FOR_DEFAULT, includeSchemaNameForDefault)
    conf(GlobalConfiguration.LIQUIBASE_CATALOG_NAME, liquibaseCatalogName)
    conf(GlobalConfiguration.LIQUIBASE_SCHEMA_NAME, liquibaseSchemaName)
    conf(GlobalConfiguration.LIQUIBASE_TABLESPACE_NAME, liquibaseTablespaceName)
    conf(GlobalConfiguration.OUTPUT_FILE_ENCODING, outputFileEncoding)
    conf(GlobalConfiguration.OUTPUT_LINE_SEPARATOR, outputLineSeparator)
    conf(GlobalConfiguration.PRESERVE_CLASSPATH_PREFIX_IN_NORMALIZED_PATHS, preserveClasspathPrefixInNormalizedPaths)
    conf(GlobalConfiguration.PRESERVE_SCHEMA_CASE, preserveSchemaCase)
    conf(GlobalConfiguration.SEARCH_PATH, searchPath)
    conf(GlobalConfiguration.SECURE_PARSING, secureParsing)
    conf(GlobalConfiguration.SHOULD_SNAPSHOT_DATA, shouldSnapshotData)
    conf(GlobalConfiguration.SHOW_BANNER, showBanner)
    conf(GlobalConfiguration.STRICT, strict)
    conf(GlobalConfiguration.SUPPORTS_METHOD_VALIDATION_LEVEL, supportsMethodValidationLevel)
    conf(GlobalConfiguration.TRIM_LOAD_DATA_FILE_HEADER, trimLoadDataFileHeader)
    conf(GlobalConfiguration.UI_SERVICE, uiService)
    conf(GlobalConfiguration.VALIDATE_XML_CHANGELOG_FILES, validateXmlChangeLogFiles)

    conf(AnalyticsArgs.CONFIG_CACHE_TIMEOUT_MILLIS, analyticsConfigCacheTimeoutMillis)
    conf(AnalyticsArgs.CONFIG_ENDPOINT_TIMEOUT_MILLIS, analyticsConfigEndpointTimeoutMillis)
    conf(AnalyticsArgs.CONFIG_ENDPOINT_URL, analyticsConfigEndpointUrl)
    conf(AnalyticsArgs.DEV_OVERRIDE, analyticsDevOverride)
    analyticsEnabled?.let {
      conf(AnalyticsArgs.ENABLED, it)

      if (!it) {
        // for good measure
        put("liquibase.plugin.${AnalyticsListener::class.qualifiedName}", NoOpAnalyticsListener::class.java)
        put("liquibase.plugin.${AnalyticsConfiguration::class.qualifiedName}", object : AnalyticsConfiguration {
          override fun getPriority() = 100 // liquibase.plugin.AbstractPluginFactory.getPlugin says higher wins
          override fun isOssAnalyticsEnabled() = false
          override fun isProAnalyticsEnabled() = false
        })
      }
    }
    conf(AnalyticsArgs.LICENSE_KEY_CHARS, analyticsLicenseKeyChars)
    conf(AnalyticsArgs.LOG_LEVEL, analyticsLogLevel)
    conf(AnalyticsArgs.TIMEOUT_MILLIS, analyticsTimeoutMillis)

    conf(LicenseTrackingArgs.ENABLED, licenseTrackingEnabled)
    conf(LicenseTrackingArgs.LOG_LEVEL, licenseTrackingLogLevel)
    conf(LicenseTrackingArgs.TIMEOUT, licenseTrackingTimeout)
    conf(LicenseTrackingArgs.TRACKING_ID, licenseTrackingTrackingId)
    conf(LicenseTrackingArgs.URL, licenseTrackingUrl)

    conf(SqlConfiguration.ALWAYS_SET_FETCH_SIZE, sqlAlwaysSetFetchSize)
    conf(SqlConfiguration.SHOW_AT_LOG_LEVEL, sqlShowAtLogLevel)
    conf(SqlConfiguration.SHOW_SQL_WARNING_MESSAGES, sqlShowSqlWarningMessages)

    putAll(additional)
  }
}

/**
 * [ref](https://docs.liquibase.com/parameters/working-with-command-parameters.html)
 */
public fun Config(block: Config.() -> Unit): Config = Config().apply(block)
