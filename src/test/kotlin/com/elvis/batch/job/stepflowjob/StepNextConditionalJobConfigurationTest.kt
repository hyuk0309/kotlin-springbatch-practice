package com.elvis.batch.job.stepflowjob

import com.elvis.batch.TestBatchConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBatchTest
@SpringBootTest(
    classes = [
        StepNextConditionalJobConfiguration::class,
        TestBatchConfig::class
    ]
)
internal class StepNextConditionalJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Test
    fun `StepNextConditionalJob_Integration_Test`() {
        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(jobExecution.stepExecutions)
            .extracting<String>(StepExecution::getStepName)
            .containsExactlyInAnyOrder(
                StepNextConditionalJobConfiguration.STEP_1_NAME,
                StepNextConditionalJobConfiguration.STEP_3_NAME
            )
    }
}