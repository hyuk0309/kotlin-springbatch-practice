package com.elvis.batch.job.writer

import com.elvis.batch.TestBatchConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBatchTest
@SpringBootTest(classes = [TestBatchConfig::class, JdbcBatchItemWriterJobConfiguration::class])
internal class CustomItemWriterJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Test
    fun customItemWriterJob_integration_test() {
        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(jobExecution.stepExecutions.toTypedArray()[0].readCount).isEqualTo(0)
    }
}
