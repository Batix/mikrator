package com.batix.mikrator

import liquibase.resource.Resource
import liquibase.resource.ResourceAccessor

internal class EmptyResourceAccessor : ResourceAccessor {
  override fun search(
    path: String?,
    recursive: Boolean,
  ): List<Resource?>? {
    return emptyList()
  }

  override fun getAll(path: String?): List<Resource?>? {
    return null
  }

  override fun describeLocations(): List<String?>? {
    return emptyList()
  }

  override fun close() {
  }
}
