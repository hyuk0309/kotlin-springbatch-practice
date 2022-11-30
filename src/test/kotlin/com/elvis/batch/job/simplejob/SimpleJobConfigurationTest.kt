package com.elvis.batch.job.simplejob

import com.elvis.batch.TestBatchConfig
import com.elvis.batch.job.simplejob.step.SimpleJobTasklet
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBatchTest
@SpringBootTest(
    classes = [
        SimpleJobConfiguration::class,
        SimpleJobTasklet::class,
        TestBatchConfig::class
    ]
)
internal class SimpleJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Test
    fun `simpleJob_통합_테스트`() {
        //given
        val now = LocalDateTime.now()

        val jobParameters = JobParametersBuilder()
            .addString("requestDate", now.toString())
            .toJobParameters()

        //when
        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        //then
        Assertions.assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
    }
}