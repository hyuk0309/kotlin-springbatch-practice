package com.elvis.batch.job.test

import com.elvis.batch.TestBatchConfig
import com.elvis.batch.TestDataSourceConfiguration
import com.elvis.batch.domain.SalesSum
import com.elvis.batch.job.test.BatchJdbcUnitTestConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.StepExecution
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.test.MetaDataInstanceFactory
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcOperations
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate
import javax.sql.DataSource

@SpringBatchTest
@ContextConfiguration(
    classes = [
        TestBatchConfig::class,
        BatchJdbcUnitTestConfiguration::class,
        TestDataSourceConfiguration::class
    ]
)
internal class BatchJdbcUnitTestConfigurationTest {

    @Autowired
    private lateinit var reader: JdbcPagingItemReader<SalesSum>

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var jdbcTemplate: JdbcOperations
    private val orderDate = LocalDate.now()

    fun getStepExecution(): StepExecution {
        val jobParameters = JobParametersBuilder()
            .addString("orderDate", this.orderDate.format(BatchJdbcUnitTestConfiguration.FORMATTER))
            .toJobParameters()

        return MetaDataInstanceFactory.createStepExecution(jobParameters)
    }

    @BeforeAll
    fun setUp() {
        this.reader.setDataSource(this.dataSource)
        this.jdbcTemplate = JdbcTemplate(this.dataSource)
    }

    @AfterAll
    fun tearDown() {
        this.jdbcTemplate.update("delete from sales")
    }

    @Test
    fun `기간내_Salses_집계되어_SalesSum이된다`(){
        //given
        val amount1 = 1000L
        val amount2 = 500L
        val amount3 = 100L

        saveSales(amount1, "1")
        saveSales(amount2, "2")
        saveSales(amount3, "3")

        //when & then
        assertThat(reader.read()!!.amountSum).isEqualTo(amount1 + amount2 + amount3)
        assertThat(reader.read()).isNull()
    }

    private fun saveSales(amount: Long, orderNo: String) {
        jdbcTemplate.update(
            "insert into sales (order_date, amount, order_no) values (?, ?, ?)",
            this.orderDate,
            amount,
            orderNo
        )
    }
}
