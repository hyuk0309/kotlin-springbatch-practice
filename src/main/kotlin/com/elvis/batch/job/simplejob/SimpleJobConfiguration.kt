package com.elvis.batch.job.simplejob

import com.elvis.batch.job.simplejob.step.SimpleJobTasklet
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

    /**
     * SimpleJob 구성
     * - simpleSte1 -> simpleStep2
     */
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
                log.info { ">>>>> This is Step2" }
                log.info { ">>>>> requestData = {$requestDate}" }
                RepeatStatus.FINISHED
            }
            .build()
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}
