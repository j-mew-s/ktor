/*
 * Copyright 2014-2021 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.server.metrics.dropwizard

import com.codahale.metrics.*
import com.codahale.metrics.MetricRegistry.*
import com.codahale.metrics.jvm.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import java.util.concurrent.*

/**
 * Metrics plugin configuration object that is used during plugin installation.
 */
public class DropwizardMetricsConfig {
    /**
     * Dropwizard metrics base name (prefix)
     */
    public var baseName: String = name("ktor.calls")

    /**
     * Dropwizard metric registry.
     */
    public var registry: MetricRegistry = MetricRegistry()

    /**
     * By default, this plugin will register `MetricSet`s from
     * [metrics-jvm](https://metrics.dropwizard.io/4.1.2/manual/jvm.html) in the configured [MetricRegistry].
     * Set this to false to not register them.
     */
    public var registerJvmMetricSets: Boolean = true
}

private class RoutingMetrics(val name: String, val context: Timer.Context)

private val routingMetricsKey = AttributeKey<RoutingMetrics>("metrics")

/**
 * Hook definition for when a routing-based call processing starts.
 */
public object RoutingCallStarted : Hook<suspend (ApplicationCall) -> Unit> {
    override fun install(application: Application, handler: suspend (ApplicationCall) -> Unit) {
        application.environment.monitor.subscribe(Routing.RoutingCallStarted, ::handler)
    }
}

/**
 * Hook definition for when a routing-based call processing finished
 */
public object RoutingCallFinished : Hook<suspend (ApplicationCall) -> Unit> {
    override fun install(application: Application, handler: suspend (ApplicationCall) -> Unit) {
        application.environment.monitor.subscribe(Routing.RoutingCallFinished, ::handler)
    }
}


public val DropwizardMetrics: ApplicationPlugin<Application, DropwizardMetricsConfig, PluginInstance> =
    createApplicationPlugin("DropwizardMetrics", ::DropwizardMetricsConfig) {
        val duration = registry.timer(name(baseName, "duration"))
        val active = registry.counter(name(baseName, "active"))
        val exceptions = registry.meter(name(baseName, "exceptions"))
        val httpStatus = ConcurrentHashMap<Int, Meter>()


        if (pluginConfig.registerJvmMetricSets) {
            listOf<Pair<String, () -> Metric>>(
                "jvm.memory" to ::MemoryUsageGaugeSet,
                "jvm.garbage" to ::GarbageCollectorMetricSet,
                "jvm.threads" to ::ThreadStatesGaugeSet,
                "jvm.files" to ::FileDescriptorRatioGauge,
                "jvm.attributes" to ::JvmAttributeGaugeSet
            ).filter { (name, _) ->
                !pluginConfig.registry.names.any { existingName -> existingName.startsWith(name) }
            }.forEach { (name, metric) -> pluginConfig.registry.register(name, metric()) }
        }

        on(CallFailed) {
            exceptions.mark()
        }

        on(RoutingCallStarted) { call ->
            val name = call.route.toString()
            val meter = plugin.registry.meter(name(plugin.baseName, name, "meter"))
            val timer = plugin.registry.timer(name(plugin.baseName, name, "timer"))
            meter.mark()
            val context = timer.time()
            call.attributes.put(
                routingMetricsKey,
                RoutingMetrics(name, context)
            )
        }

        on(RoutingCallFinished) { call ->
            val routingMetrics = call.attributes.take(routingMetricsKey)
            val status = call.response.status()?.value ?: 0
            val statusMeter = plugin.registry.meter(name(plugin.baseName, routingMetrics.name, status.toString()))
            statusMeter.mark()
            routingMetrics.context.stop()
        }
    }
