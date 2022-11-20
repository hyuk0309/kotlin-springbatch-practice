package com.example.kotlinspringbatchpractice.job

import com.example.kotlinspringbatchpractice.domain.Teacher
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.batch.item.support.CompositeItemProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory


@Configuration
class ProcessorCompositeJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val emf: EntityManagerFactory
) {

    companion object {
        private const val JOB_NAME = "processorCompositeBatch"
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
            .chunk<Teacher, String>(CHUNK_SIZE)
            .reader(reader())
            .processor(compositeProcessor())
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

    @Bean
    fun compositeProcessor(): CompositeItemProcessor<Teacher, String> {
        val delegates: MutableList<ItemProcessor<*, *>> = ArrayList(2)
        delegates.add(processor1())
        delegates.add(processor2())

        val processor = CompositeItemProcessor<Teacher, String>()
        processor.setDelegates(delegates)

        return processor
    }

    fun processor1(): ItemProcessor<Teacher, String> {
        return ItemProcessor { teacher ->
            teacher.name
        }
    }

    fun processor2(): ItemProcessor<String, String> {
        return ItemProcessor { name ->
            "안녕하세요 $name 입니다."
        }
    }

    fun writer(): ItemWriter<String> {
        return ItemWriter { items ->
            for (item in items)
                logger.info(item)
        }
    }
}
