package io.ktor.utils.io.core

import io.ktor.utils.io.core.internal.*
import kotlin.native.concurrent.*

public actual val PACKET_MAX_COPY_SIZE: Int = 200

public actual fun BytePacketBuilder(headerSizeHint: Int): BytePacketBuilder =
    BytePacketBuilder(headerSizeHint, ChunkBuffer.Pool)

public actual typealias EOFException = io.ktor.utils.io.errors.EOFException
