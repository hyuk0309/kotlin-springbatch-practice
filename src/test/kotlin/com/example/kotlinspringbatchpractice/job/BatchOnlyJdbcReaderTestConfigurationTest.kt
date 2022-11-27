package com.example.kotlinspringbatchpractice.job

import com.example.kotlinspringbatchpractice.TestDataSourceConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDate
import javax.sql.DataSource

internal class BatchOnlyJdbcReaderTestConfigurationTest {

    @Autowired
    private lateinit var dataSource: DataSource

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var context: ConfigurableApplicationContext

    @Autowired
    private lateinit var orderDate: LocalDate

    @Autowired
    private lateinit var job: BatchOnlyJdbcReaderTestConfiguration

    @BeforeAll
    fun setUp() {
        this.context = AnnotationConfigApplicationContext(TestDataSourceConfiguration::class.java)
        this.dataSource = context.getBean("dataSource") as DataSource
        this.jdbcTemplate = JdbcTemplate(this.dataSource)
        this.orderDate = LocalDate.of(2022, 11, 24)
        this.job = BatchOnlyJdbcReaderTestConfiguration(dataSource)
    }

    @AfterAll
    fun tearDown() {
        context.close()
    }

    @Test
    fun `기간내_Sales_집계되어_SalesSum이된다`() {
        //given
        val amount1 = 1000L
        val amount2 = 100L
        val amount3 = 10L
        jdbcTemplate.update(
            "insert into sales (order_date, amount, order_no) values (?, ?, ?)",
            orderDate,
            amount1,
            "1"
        )
        jdbcTemplate.update(
            "insert into sales (order_date, amount, order_no) values (?, ?, ?)",
            orderDate,
            amount2,
            "2"
        )
        jdbcTemplate.update(
            "insert into sales (order_date, amount, order_no) values (?, ?, ?)",
            orderDate,
            amount3,
            "3"
        )

        val reader =
            job.batchOnlyJdbcReaderTestJobReader()
        reader.afterPropertiesSet()

        //when & then
        assertThat(reader.read()!!.amountSum).isEqualTo(amount1 + amount2 + amount3)
        assertThat(reader.read()).isNull()
    }
}