package com.yaabelozerov.superfinancer.data.remote

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.max

class RateLimiter(eventsPerMinute: Int) {
    private val mutex = Mutex()

    @Volatile private var next = Long.MIN_VALUE
    private val delayNanos = 1_000_000_000L / eventsPerMinute * 60

    suspend fun acquire() {
        val now = System.nanoTime()
        val until = mutex.withLock {
            max(next, now).also {
                next += it + delayNanos
            }
        }
        if (until != now) {
            delay((until - now) / 1_000_000)
        }
    }
}