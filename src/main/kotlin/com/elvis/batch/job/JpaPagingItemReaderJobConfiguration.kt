package com.elvis.batch.job

import com.elvis.batch.domain.Pay
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
class JpaPagingItemReaderJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory
) {

    companion object {
        const val CHUNK_SIZE = 10
        val logger = KotlinLogging.logger {}
    }

    @Bean
    fun jpaPagingItemReaderJob(): Job {
        return jobBuilderFactory.get("jpaPagingItemReaderJob")
            .start(jpaPagingItemReaderStep())
            .build()
    }

    @Bean
    fun jpaPagingItemReaderStep(): Step {
        return stepBuilderFactory.get("jpaPaingItemReaderStep")
            .chunk<Pay, Pay>(CHUNK_SIZE)
            .reader(jpaPagingItemReader())
            .writer { list ->
                for (pay in list)
                    logger.info { "Current Pay={$pay}" }
            }
            .build()
    }

    @Bean
    fun jpaPagingItemReader(): JpaPagingItemReader<Pay> {
        return JpaPagingItemReaderBuilder<Pay>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString("SELECT p FROM Pay p WHERE amount >= 2000")
            .build()
    }
}
