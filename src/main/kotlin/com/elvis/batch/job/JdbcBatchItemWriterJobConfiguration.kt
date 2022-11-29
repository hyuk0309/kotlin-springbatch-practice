package com.elvis.batch.job

import com.elvis.batch.domain.Pay
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import javax.sql.DataSource

@Configuration
class JdbcBatchItemWriterJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val dataSource: DataSource
) {

    companion object {
        val logger = KotlinLogging.logger {}
        const val CHUCK_SIZE = 10
    }

    @Bean
    fun jdbcBatchItemWriterJob(): Job {
        return jobBuilderFactory.get("jdbcBatchItemWriterJob")
            .start(jdbcBatchItemWriterStep())
            .build()
    }

    @Bean
    fun jdbcBatchItemWriterStep(): Step {
        return stepBuilderFactory.get("jdbcBatchWriterStep")
            .chunk<Pay, Pay>(CHUCK_SIZE)
            .reader(jdbcBatchItemWriterReader())
            .writer(jdbcBatchItemWriter())
            .build()
    }

    @Bean
    fun jdbcBatchItemWriterReader(): JdbcCursorItemReader<Pay> {
        return JdbcCursorItemReaderBuilder<Pay>()
            .fetchSize(CHUCK_SIZE)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(Pay::class.java))
            .sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
            .name("jdbcBatchItemWriter")
            .build()
    }

    @Bean
    fun jdbcBatchItemWriter(): JdbcBatchItemWriter<Pay> {
        return JdbcBatchItemWriterBuilder<Pay>()
            .dataSource(dataSource)
            .sql("insert into pay2(amount, tx_name, tx_date_time) values (:amount, :txName, :txDateTime)")
            .beanMapped()
            .build()
    }
}
