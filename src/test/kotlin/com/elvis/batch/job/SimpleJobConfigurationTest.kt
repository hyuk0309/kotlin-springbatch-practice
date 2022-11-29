package com.elvis.batch.job

import com.example.batch.TestBatchConfig
import com.example.batch.job.step.SimpleJobTasklet
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import javax.batch.runtime.BatchStatus

@SpringBatchTest
@SpringBootTest(classes = [SimpleJobConfiguration::class, TestBatchConfig::class, SimpleJobTasklet::class])
internal class SimpleJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Disabled("DB 용량 아끼기")
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
        assertThat(jobExecution.status.toString()).isEqualTo(BatchStatus.COMPLETED.toString())
    }
}