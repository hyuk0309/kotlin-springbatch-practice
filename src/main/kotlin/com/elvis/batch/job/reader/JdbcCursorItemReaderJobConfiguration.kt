package com.elvis.batch.job.reader

import com.elvis.batch.domain.Pay
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import javax.sql.DataSource

@Configuration
class JdbcCursorItemReaderJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val dateSource: DataSource,
) {
    private val log = KotlinLogging.logger {}

    @Bean
    fun jobCursorItemReaderJob(): Job {
        return jobBuilderFactory.get(JOB_NAME)
            .start(jdbcCursorItemReaderStep())
            .build()
    }

    @Bean
    @JobScope
    fun jdbcCursorItemReaderStep(): Step {
        return stepBuilderFactory.get(STEP_NAME)
            .chunk<Pay, Pay>(CHUNK_SIZE)
            .reader(jdbcCursorItemReader())
            .writer(jdbcCursorItemWriter())
            .build()
    }

    @Bean //TODO: resolve @StepScope issue
    fun jdbcCursorItemReader(): JdbcCursorItemReader<Pay> {
        return JdbcCursorItemReaderBuilder<Pay>()
            .name(READER_NAME)
            .fetchSize(CHUNK_SIZE)
            .dataSource(dateSource)
            .rowMapper(BeanPropertyRowMapper(Pay::class.java))
            .sql("SELECT id, amount, tx_name, tx_date_time FROM pay")
            .build()
    }

    @Bean
    @StepScope
    fun jdbcCursorItemWriter(): ItemWriter<Pay> {
        return ItemWriter { list ->
            log.info { "I'm item writer in spring batch framework." }
            for (pay in list)
                log.info { "Current Pay={$pay}" }
        }
    }

    companion object {
        const val JOB_NAME = "jdbcCursorItemReaderJob"
        const val STEP_NAME = "jdbcCursorItemReaderStep"
        const val READER_NAME = "jdbcCursorItemReader"

        const val CHUNK_SIZE = 10
    }
}
