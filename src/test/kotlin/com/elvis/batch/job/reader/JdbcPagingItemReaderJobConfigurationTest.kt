package com.elvis.batch.job.reader

import com.elvis.batch.TestBatchConfig
import org.assertj.core.api.Assertions.*
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
@SpringBootTest(classes = [TestBatchConfig::class, JdbcPagingItemReaderJobConfiguration::class])
internal class JdbcPagingItemReaderJobConfigurationTest {

    @Autowired
    private lateinit var jobLauncherTestUtils: JobLauncherTestUtils

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var jdbcTemplate: JdbcOperations

    @BeforeAll
    fun setUp() {
        this.jdbcTemplate = JdbcTemplate(this.dataSource)
    }

    @Test
    fun jdbcPagingItemReaderJob_integration_test() {
        //given
        val amount1 = 3000L
        val amount2 = 200L
        val amount3 = 100L

        savePay(amount1, "tax")
        savePay(amount2, "tax")
        savePay(amount3, "tax")

        //when
        val jobExecution = jobLauncherTestUtils.launchJob()

        //then
        assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
        assertThat(jobExecution.stepExecutions.toTypedArray()[0].readCount).isEqualTo(1)
    }

    private fun savePay(amount: Long, txName: String) {
        val update = jdbcTemplate.update(
            "insert into pay (amount, tx_name, tx_date_time) values (?, ?, ?)",
            amount,
            txName,
            LocalDate.now()
        )
    }
}
