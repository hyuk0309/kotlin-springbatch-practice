package com.example.kotlinspringbatchpractice.job

import com.example.kotlinspringbatchpractice.domain.Pay
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import javax.sql.DataSource

@Configuration
class JdbcCursorItemReaderJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val dateSource: DataSource
) {
    private val logger = KotlinLogging.logger {}

    companion object {
        const val CHUNK_SIZE = 10
    }

    @Bean
    fun jobCursorItemReaderJob(): Job {
        return jobBuilderFactory.get("jdbcCursorItemReaderJob")
            .start(jdbcCursorItemReaderStep())
            .build()
    }

    @Bean
    fun jdbcCursorItemReaderStep(): Step {
        return stepBuilderFactory.get("jdbcCursorItemReaderStep")
            .chunk<Pay, Pay>(CHUNK_SIZE)
            .reader(jdbcCursorItemReader())
            .writer {list ->
                for (pay in list)
                    logger.info { "Current Pay={$pay}" }  // TODO 데이터는 잘 불러오는데, 매핑된 객체가 null을 갖고 있음
            }
            .build()
    }

    @Bean
    fun jdbcCursorItemReader(): JdbcCursorItemReader<Pay> {
        return JdbcCursorItemReaderBuilder<Pay>()
            .fetchSize(CHUNK_SIZE)
            .dataSource(dateSource)
            .rowMapper(BeanPropertyRowMapper(Pay::class.java))
            .sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
            .name("jdbcCursorItemReader")
            .build()
    }
}