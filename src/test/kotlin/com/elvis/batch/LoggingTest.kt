package com.elvis.batch

import mu.KotlinLogging
import org.junit.jupiter.api.Test

class LoggingTest {

    private val logger = KotlinLogging.logger {}

    @Test
    fun 로그_성공() {
        logger.info {"Test Logging"}
    }
}