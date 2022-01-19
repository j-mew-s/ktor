/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.server.http.content

import io.ktor.http.content.*
import io.ktor.server.application.*
import java.io.*

/**
 * Default outgoing content transformation
 */
internal actual fun platformTransformDefaultContent(
    call: ApplicationCall,
    value: Any
): OutgoingContent? = when (value) {
    is URIFileContent -> {
        when (value.uri.scheme) {
            "file" -> LocalFileContent(File(value.uri))
            else -> null
        }
    }
    else -> null
}
