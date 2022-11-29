package com.elvis.batch.job

import com.example.batch.domain.Teacher
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
class ProcessNullJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val emf: EntityManagerFactory
) {

    companion object {
        private const val JOB_NAME = "processNullBatch"
        private const val BEAN_PREFIX = JOB_NAME + "_"
        private const val CHUNK_SIZE = 10

        private val logger = KotlinLogging.logger {}
    }

    @Bean(JOB_NAME)
    fun job(): Job {
        return jobBuilderFactory.get(JOB_NAME)
            .preventRestart()
            .start(step())
            .build()
    }

    @Bean(BEAN_PREFIX + "step")
    fun step(): Step {
        return stepBuilderFactory.get(BEAN_PREFIX + "step")
            .chunk<Teacher, Teacher>(CHUNK_SIZE)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .build()
    }

    @Bean(BEAN_PREFIX + "reader")
    fun reader(): JpaPagingItemReader<Teacher> {
        return JpaPagingItemReaderBuilder<Teacher>()
            .name(BEAN_PREFIX + "reader")
            .entityManagerFactory(emf)
            .pageSize(CHUNK_SIZE)
            .queryString("SELECT t FROM Teacher t")
            .build()
    }

    @Bean(BEAN_PREFIX + "processor")
    fun processor(): ItemProcessor<Teacher, Teacher> {
        return ItemProcessor { teacher ->

            val isIgnoreTarget = teacher.id!! % 2 == 0L

            if (isIgnoreTarget) {
                logger.info(">>>>>> Teacher name = {${teacher.name}}, isIgnoreTarget={$isIgnoreTarget}")
                null
            } else {
                teacher
            }
        }
    }

    private fun writer(): ItemWriter<Teacher> {
        return ItemWriter { items ->
            for (item in items)
                logger.info("Teacher Name={${item.name}}")
        }
    }
}
