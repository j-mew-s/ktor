/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.server.application.internal

/**
 * Internal helper for setting cause on [Throwable] in MPP
 */
public expect fun Throwable.initCauseBridge(cause: Throwable)
