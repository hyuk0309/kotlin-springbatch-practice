package com.elvis.batch.job.stepflowjob

import com.elvis.batch.TestBatchConfig
import com.elvis.batch.job.stepflowjob.listener.SkipCheckingListener
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBatchTest
@SpringBootTest(
    classes = [
        StepNextConditionalJob2Configuration::class,
        TestBatchConfig::class
    ]
)
@Import(SkipCheckingListener::class)
internal class StepNextConditionalJob2ConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Test
    fun `StepNextConditionalJob2_IntegrationTest_Success`() {
        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(jobExecution.stepExecutions)
            .extracting<String>(StepExecution::getStepName)
            .containsExactlyInAnyOrder(
                StepNextConditionalJob2Configuration.STEP_1_NAME,
                StepNextConditionalJob2Configuration.STEP_3_NAME
            )
    }
}