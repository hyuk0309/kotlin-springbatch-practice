package com.elvis.batch.job

import com.elvis.batch.domain.Pay
import com.elvis.batch.domain.Pay2
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory


@Configuration
class JpaItemWriterJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory
) {

    companion object {
        val logger = KotlinLogging.logger {}
        const val CHUNK_SIZE = 10
    }

    @Bean
    fun jpaItemWriterJob(): Job {
        return jobBuilderFactory.get("jpaItemWriterJob")
            .start(jpaItemWriterStep())
            .build()
    }

    @Bean
    fun jpaItemWriterStep(): Step {
        return stepBuilderFactory.get("jpaItemWriterStep")
            .chunk<Pay, Pay2>(CHUNK_SIZE)
            .reader(jpaItemWriterReader())
            .processor(jpaItemProcessor())
            .writer(jpaItemWriter())
            .build()
    }

    @Bean
    fun jpaItemWriterReader(): JpaPagingItemReader<Pay> {
        return JpaPagingItemReaderBuilder<Pay>()
            .name("jpaItemWriterReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString("SELECT p FROM Pay p")
            .build()
    }

    @Bean
    fun jpaItemProcessor(): ItemProcessor<Pay, Pay2?> {
        return ItemProcessor { pay ->
            Pay2(pay.amount, pay.txName, pay.txDateTime)
        }
    }

    @Bean
    fun jpaItemWriter(): JpaItemWriter<Pay2> {
        val jpaItemWriter = JpaItemWriter<Pay2>()
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory)
        return jpaItemWriter
    }
}
