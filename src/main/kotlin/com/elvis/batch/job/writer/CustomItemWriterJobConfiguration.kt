package com.elvis.batch.job.writer

import com.elvis.batch.domain.Pay
import com.elvis.batch.domain.Pay2
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory

@Configuration
class CustomItemWriterJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val entityManagerFactory: EntityManagerFactory
) {
    @Bean
    fun customItemWriterJob(): Job {
        return jobBuilderFactory.get("customItemWriterJob")
            .start(customItemWriterStep())
            .build()
    }

    @Bean
    fun customItemWriterStep(): Step {
        return stepBuilderFactory.get("customItemWriterStep")
            .chunk<Pay, Pay2>(CHUNK_SIZE)
            .reader(customItemWriterReader())
            .processor(customItemWriterProcessor())
            .writer(customItemWriter())
            .build()
    }

    @Bean
    fun customItemWriterReader(): JpaPagingItemReader<Pay> {
        return JpaPagingItemReaderBuilder<Pay>()
            .name("customItemWriterReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString("SELECT p FROM Pay p")
            .build()
    }

    @Bean
    fun customItemWriterProcessor(): ItemProcessor<Pay, Pay2> {
        return ItemProcessor { pay ->
            Pay2(pay.amount, pay.txName, pay.txDateTime)
        }
    }

    @Bean
    fun customItemWriter(): ItemWriter<Pay2> {
        return ItemWriter { items ->
            for (item in items) {
                println(item)
            }
        }
    }

    companion object {
        val log = KotlinLogging.logger {}
        const val CHUNK_SIZE = 10
    }
}
