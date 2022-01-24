/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.tests.server.plugins

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.plugins.*
import io.ktor.plugins.spa.*
import io.ktor.server.testing.*
import kotlin.test.*

class SinglePageApplicationTest {
    @Test
    fun testPageGet() = testApplication {
        install(SinglePageApplication) {
            filesPath = "jvm/test/io/ktor/tests/server/plugins"
            applicationRoute = "selected"
            defaultPage = "CORSTest.kt"
        }

        client.get("/selected/StatusPageTest.kt").let {
            assertEquals(it.status, HttpStatusCode.OK)
        }

        client.get("/selected").let {
            assertEquals(it.status, HttpStatusCode.OK)
        }
    }

    @Test
    fun testIgnoreRoutes() = testApplication {
        install(SinglePageApplication) {
            filesPath = "jvm/test/io/ktor/tests/server/plugins"
            defaultPage = "CORSTest.kt"
            ignoreFiles { it.contains("CallIdTest.kt") }
            ignoreFiles { it.endsWith("ContentTest.kt") }
        }

        client.get("/StatusPageTest.kt").let {
            assertEquals(it.status, HttpStatusCode.OK)
        }

        client.get("/").let {
            assertEquals(it.status, HttpStatusCode.OK)
        }

        assertFailsWith<ClientRequestException> {
            client.get("/CallIdTest.kt")
        }

        assertFailsWith<ClientRequestException> {
            client.get("/PartialContentTest.kt")
        }
    }

    @Test
    fun testIgnoreAllRoutes() = testApplication {
        install(SinglePageApplication) {
            filesPath = "jvm/test/io/ktor/tests/server/plugins"
            defaultPage = "CORSTest.kt"
            ignoreFiles { true }
        }
        assertFailsWith<ClientRequestException> {
            client.get("/CallIdTest.kt")
        }

        assertFailsWith<ClientRequestException> {
            client.get("/")
        }
    }

    @Test
    fun testResources() = testApplication {
        install(SinglePageApplication) {
            useResources = true
            filesPath = "io.ktor.tests.server.plugins"
            defaultPage = "CORSTest.class"
        }

        client.get("/StaticContentTest.class").let {
            assertEquals(it.status, HttpStatusCode.OK)
        }

        client.get("/").let {
            assertEquals(it.status, HttpStatusCode.OK)
        }
    }

    @Test
    fun testIgnoreResourceRoutes() = testApplication {
        install(SinglePageApplication) {
            useResources = true
            filesPath = "io.ktor.tests.server.plugins"
            defaultPage = "CORSTest.class"
            ignoreFiles { it.contains("CallIdTest.class") }
            ignoreFiles { it.endsWith("ContentTest.class") }
        }

        client.get("/StatusPageTest.class").let {
            assertEquals(it.status, HttpStatusCode.OK)
        }

        client.get("/").let {
            assertEquals(it.status, HttpStatusCode.OK)
        }

        assertFailsWith<ClientRequestException> {
            client.get("/CallIdTest.class")
        }

        assertFailsWith<ClientRequestException> {
            client.get("/PartialContentTest.txt")
        }
    }

    @Test
    fun testIgnoreAllResourceRoutes() = testApplication {
        install(SinglePageApplication) {
            useResources = true
            filesPath = "io.ktor.tests.server.plugins"
            defaultPage = "CORSTest.class"
            ignoreFiles { true }
        }
        assertFailsWith<ClientRequestException> {
            client.get("/CallIdTest.class")
        }

        assertFailsWith<ClientRequestException> {
            client.get("/")
        }
    }

    @Test
    fun testShortcut() = testApplication {
        install(SinglePageApplication) {
            angular("jvm/test/io/ktor/tests/server/plugins")
        }

        client.get("/StatusPageTest.kt").let {
            assertEquals(it.status, HttpStatusCode.OK)
        }
    }
}
