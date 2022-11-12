package com.example.kotlinspringbatchpractice.job

import mu.KotlinLogging
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StepNextConditionalJobConfiguration(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory
) {
    private val logger = KotlinLogging.logger {}

    @Bean
    fun stepNextConditionalJob(): Job {
        return jobBuilderFactory.get("stepNextConditionalJob")
            .start(conditionalJobStep1())
                .on("FAILED")
                .to(conditionalJobStep3())
                .on("*")
                .end()
            .from(conditionalJobStep1())
                .on("*")
                .to(conditionalJobStep2())
                .next(conditionalJobStep3())
                .on("*")
                .end()
            .end()
            .build()
    }

    @Bean
    fun conditionalJobStep1(): Step {
        return stepBuilderFactory.get("conditionalJobStep1")
            .tasklet { contribution, _ ->
                logger.info { ">>>>> This is stepNextConditionalJob Step1" }

                /**
                 * ExitStatus를 Fail로 지정.
                 * 해당 status를 보고 flow 진행.
                 */
//                contribution.exitStatus = ExitStatus.FAILED

                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun conditionalJobStep2(): Step {
        return stepBuilderFactory.get("conditionalJobStep2")
            .tasklet { _, _ ->
                logger.info { ">>>>> This is stepNextConditionalJob Step2" }
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun conditionalJobStep3(): Step {
        return stepBuilderFactory.get("conditionalJobStep3")
            .tasklet { _, _ ->
                logger.info { ">>>>> This is stepNextConditionalJob Step3" }
                RepeatStatus.FINISHED
            }
            .build()
    }
}