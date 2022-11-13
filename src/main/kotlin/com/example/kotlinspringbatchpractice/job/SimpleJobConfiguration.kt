package com.example.kotlinspringbatchpractice.job

import com.example.kotlinspringbatchpractice.job.step.SimpleJobTasklet
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SimpleJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val simpleJobTasklet: SimpleJobTasklet
) {
    private val logger = KotlinLogging.logger {}

    @Bean
    fun simpleJob(): Job {
        return jobBuilderFactory.get("simpleJob")
            .start(simpleStep1())
            .next(simpleStep2(null))
            .build()
    }

    @Bean
    @JobScope
    fun simpleStep1(): Step {
        return stepBuilderFactory.get("simpleStep1")
            .tasklet(simpleJobTasklet)
            .build()
    }

    @Bean
    @JobScope
    fun simpleStep2(@Value("#{jobParameters[requestDate]}") requestDate: String?): Step {
        return stepBuilderFactory.get("simpleStep2")
            .tasklet { _, _ ->
                logger.info { ">>>>> This is Step2" }
                logger.info { ">>>>> requestData = {$requestDate}" }
                RepeatStatus.FINISHED
            }
            .build()
    }
}