package com.example.kotlinspringbatchpractice.job

import org.apache.commons.logging.LogFactory
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
) {
    private val log = LogFactory.getLog(SimpleJobConfiguration::class.java)

    @Bean
    fun simpleJob(): Job {
        return jobBuilderFactory.get("simpleJob")
            .start(simpleStep(null))
            .build()
    }

    @Bean
    @JobScope
    fun simpleStep(@Value("#{jobParameters[requestDate]}")requestDate: String?): Step {
        return stepBuilderFactory.get("simpleStep")
            .tasklet { _, _ ->
                log.info { ">>>>> This is Step1" }
                log.info { ">>>>> requestData = {$requestDate}" }
                RepeatStatus.FINISHED
            }
            .build()
    }
}