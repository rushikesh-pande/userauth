package com.auth.security.db.service;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Database Optimisation Enhancement: Database Metrics Service
 *
 * Tracks cache and query performance metrics for userauth.
 * Exposed to Prometheus via /actuator/prometheus.
 *
 * Metrics:
 *  - userauth_cache_hits_total       — Redis cache hits
 *  - userauth_cache_misses_total     — Redis cache misses (DB queries)
 *  - userauth_db_queries_total       — Total DB queries by type
 *  - userauth_db_slow_queries_total  — Queries above 500ms
 *  - userauth_connection_pool_active — HikariCP active connections
 */
@Service
public class DatabaseMetricsService {

    private final MeterRegistry meterRegistry;
    private final AtomicLong activeConnections = new AtomicLong(0);

    public DatabaseMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        Gauge.builder("userauth.connection.pool.active", activeConnections, AtomicLong::get)
             .description("Active HikariCP connections for userauth")
             .tag("service", "userauth")
             .register(meterRegistry);
    }

    public void recordCacheHit(String cacheName) {
        Counter.builder("userauth.cache.hits.total")
               .tag("service", "userauth").tag("cache", cacheName)
               .description("Redis cache hits for userauth")
               .register(meterRegistry).increment();
    }

    public void recordCacheMiss(String cacheName) {
        Counter.builder("userauth.cache.misses.total")
               .tag("service", "userauth").tag("cache", cacheName)
               .description("Redis cache misses for userauth (DB fallback)")
               .register(meterRegistry).increment();
    }

    public void recordDbQuery(String queryType) {
        Counter.builder("userauth.db.queries.total")
               .tag("service", "userauth").tag("type", queryType)
               .description("DB queries for userauth")
               .register(meterRegistry).increment();
    }

    public void recordSlowQuery(String queryType, long ms) {
        Counter.builder("userauth.db.slow.queries.total")
               .tag("service", "userauth").tag("type", queryType)
               .description("DB queries exceeding 500ms for userauth")
               .register(meterRegistry).increment();
        meterRegistry.summary("userauth.db.query.duration",
                "service", "userauth", "type", queryType).record(ms);
    }

    public void setActiveConnections(long count) {
        activeConnections.set(count);
    }
}
