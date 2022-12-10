package com.elvis.batch.job.writer

import com.elvis.batch.TestBatchConfig
import com.elvis.batch.domain.Pay
import com.elvis.batch.domain.Pay2Repository
import com.elvis.batch.domain.PayRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBatchTest
@SpringBootTest(classes = [TestBatchConfig::class, JpaItemWriterJobConfiguration::class])
internal class JpaItemWriterJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var payRepository: PayRepository

    @Autowired
    private lateinit var pay2Repository: Pay2Repository

    @Test
    fun jpaItemWriterJob_integration_test() {
        //given
        val pay1 = Pay(amount = 3000L, txName = "name1", txDateTime = LocalDateTime.now())
        val pay2 = Pay(amount = 1000L, txName = "name2", txDateTime = LocalDateTime.now())
        val pay3 = Pay(amount = 800L, txName = "name3", txDateTime = LocalDateTime.now())

        payRepository.saveAll(arrayListOf(pay1, pay2, pay3))

        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(pay2Repository.findAll()).hasSize(3)
    }
}
