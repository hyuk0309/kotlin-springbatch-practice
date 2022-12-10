package com.elvis.batch.job.writer

import com.elvis.batch.TestBatchConfig
import com.elvis.batch.domain.Student
import com.elvis.batch.domain.StudentRepository
import com.elvis.batch.domain.Teacher
import com.elvis.batch.domain.TeacherRepository
import com.elvis.batch.job.processor.ProcessNullJobConfiguration
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
@SpringBootTest(classes = [TestBatchConfig::class, TransactionWriterJobConfiguration::class])
internal class TransactionWriterJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var teacherRepository: TeacherRepository

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @AfterAll
    fun cleanUp() {
        studentRepository.deleteAllInBatch()
        teacherRepository.deleteAllInBatch()
    }

    @Test
    fun transactionWriterJob_integration_test() {
        //given
        val teacher = Teacher("winter", "programming", arrayListOf())
        val student = Student("messi", teacher)
        teacher.students.add(student)

        teacherRepository.save(teacher)

        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
    }
}
