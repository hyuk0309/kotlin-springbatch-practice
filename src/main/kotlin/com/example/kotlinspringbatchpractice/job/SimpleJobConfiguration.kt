package com.example.kotlinspringbatchpractice.job

import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SimpleJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
) {
    private val log = LogFactory.getLog(SimpleJobConfiguration::class.java)

    @Bean
    fun simpleJob(): Job {
        return jobBuilderFactory.get("simpleJob")
            .start(simpleStep())
            .build()
    }

    @Bean
    fun simpleStep(): Step {
        return stepBuilderFactory.get("simpleStep")
            .tasklet { _, _ ->
                log.info { ">>>>> This is Step1" }
                RepeatStatus.FINISHED
            }
            .build()
    }
}