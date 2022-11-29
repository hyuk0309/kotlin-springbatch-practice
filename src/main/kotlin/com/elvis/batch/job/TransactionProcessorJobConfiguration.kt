package com.elvis.batch.job

import com.example.batch.domain.ClassInformation
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
class TransactionProcessorJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val emf: EntityManagerFactory
) {

    companion object {
        private const val JOB_NAME = "transactionProcessorJob"
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
            .chunk<Teacher, ClassInformation>(CHUNK_SIZE)
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

    private fun processor(): ItemProcessor<Teacher, ClassInformation> {
        return ItemProcessor { teacher ->
            ClassInformation(teacher.name, teacher.students.size)
        }
    }

    private fun writer(): ItemWriter<ClassInformation> {
        return ItemWriter { items ->
            logger.info(">>>>>>> ItemWriter")
            for (item in items) {
                logger.info("빈 정보={$item}")
            }
        }
    }
}
