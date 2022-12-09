package com.elvis.batch.job.reader

import com.elvis.batch.TestBatchConfig
import com.elvis.batch.domain.Pay
import com.elvis.batch.domain.PayRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBatchTest
@SpringBootTest(classes = [TestBatchConfig::class, JpaPagingItemReaderJobConfiguration::class])
internal class JpaPagingItemReaderJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var payRepository: PayRepository

    @AfterAll
    fun cleanUp() {
        payRepository.deleteAllInBatch()
    }

    @Test
    fun jpaPagingItemReader_integration_test() {
        //given
        val pay1 = Pay(amount = 3000L, txName = "name1", txDateTime = LocalDateTime.now())
        val pay2 = Pay(amount = 1000L, txName = "name2", txDateTime = LocalDateTime.now())
        val pay3 = Pay(amount = 800L, txName = "name3", txDateTime = LocalDateTime.now())

        payRepository.saveAll(arrayListOf(pay1, pay2, pay3))

        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(jobExecution.stepExecutions.toTypedArray()[0].readCount).isEqualTo(1)
    }
}