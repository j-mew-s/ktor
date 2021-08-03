/*
* Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
*/

@file:Suppress("DEPRECATION_ERROR")

package io.ktor.config

@Deprecated(
    message = "Moved to io.ktor.server.config",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("ApplicationConfig", "io.ktor.server.config.*")
)
public interface ApplicationConfig

@Deprecated(
    message = "Moved to io.ktor.server.config",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("ApplicationConfigValue", "io.ktor.server.config.*")
)
public interface ApplicationConfigValue

@Deprecated(
    message = "Moved to io.ktor.server.config",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("ApplicationConfigurationException", "io.ktor.server.config.*")
)
public class ApplicationConfigurationException(message: String) : Exception(message)
