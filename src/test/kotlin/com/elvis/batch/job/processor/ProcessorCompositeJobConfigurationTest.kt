package com.elvis.batch.job.processor

import com.elvis.batch.TestBatchConfig
import com.elvis.batch.domain.Teacher
import com.elvis.batch.domain.TeacherRepository
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBatchTest
@SpringBootTest(classes = [TestBatchConfig::class, ProcessorCompositeJobConfiguration::class])
internal class ProcessorCompositeJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var teacherRepository: TeacherRepository

    @AfterAll
    fun cleanUp() {
        teacherRepository.deleteAllInBatch()
    }

    @Test
    fun processorCompositeJob_integration_test() {
        //given
        val teacher = Teacher("winter", "kotlin")
        teacherRepository.save(teacher)

        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(jobExecution.stepExecutions.toTypedArray()[0].readCount).isEqualTo(1)
        assertThat(jobExecution.stepExecutions.toTypedArray()[0].writeCount).isEqualTo(1)
    }
}
