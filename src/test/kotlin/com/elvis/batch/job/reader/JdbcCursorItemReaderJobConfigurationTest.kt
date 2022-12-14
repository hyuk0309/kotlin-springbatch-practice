package com.elvis.batch.job.reader

import com.elvis.batch.TestBatchConfig
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.batch.core.BatchStatus
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDate
import javax.sql.DataSource


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBatchTest
@SpringBootTest(
    classes = [
        JdbcCursorItemReaderJobConfiguration::class,
        TestBatchConfig::class
    ]
)
internal class JdbcCursorItemReaderJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var jdbcTemplate: JdbcOperations

    private val log = KotlinLogging.logger {}

    @BeforeAll
    fun setUp() {
        this.jdbcTemplate = JdbcTemplate(this.dataSource)
    }

    @Test
    fun jdbcCursorItemReaderJob_integration_test() {
        //given
        val amount1 = 1000L
        val amount2 = 500L
        val amount3 = 100L

        savePay(amount1, "tax")
        savePay(amount2, "tax")
        savePay(amount3, "tax")

        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(jobExecution.stepExecutions.toTypedArray()[0].readCount).isEqualTo(3)
    }

    private fun savePay(amount: Long, txName: String) {
        val update = jdbcTemplate.update(
            "insert into pay (amount, tx_name, tx_date_time) values (?, ?, ?)",
            amount,
            txName,
            LocalDate.now()
        )

        log.info {"update : $update"}
    }
}
