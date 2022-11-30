package com.elvis.batch.job.stepflowjob

import com.elvis.batch.TestBatchConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.batch.runtime.BatchStatus

@SpringBatchTest
@SpringBootTest(
    classes = [
        StepNextJobConfiguration::class,
        TestBatchConfig::class
    ]
)
internal class StepNextJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Test
    fun `StepNextJob_IntegrationTest_Success`() {
        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status.toString()).isEqualTo(BatchStatus.COMPLETED.toString())
    }
}