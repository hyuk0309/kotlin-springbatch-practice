package com.elvis.batch.job.stepflowjob

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
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {
    @Bean
    fun stepNextConditionalJob(): Job {
        return jobBuilderFactory.get(JOB_NAME)
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
        return stepBuilderFactory.get(STEP_1_NAME)
            .tasklet { contribution, _ ->
                log.info { ">>>>> This is stepNextConditionalJob Step1" }

                /**
                 * ExitStatus를 Fail로 지정.
                 * 해당 status를 보고 flow 진행.
                 */
                contribution.exitStatus = ExitStatus.FAILED

                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun conditionalJobStep2(): Step {
        return stepBuilderFactory.get(STEP_2_NAME)
            .tasklet { _, _ ->
                log.info { ">>>>> This is stepNextConditionalJob Step2" }
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun conditionalJobStep3(): Step {
        return stepBuilderFactory.get(STEP_3_NAME)
            .tasklet { _, _ ->
                log.info { ">>>>> This is stepNextConditionalJob Step3" }
                RepeatStatus.FINISHED
            }
            .build()
    }

    companion object {
        const val JOB_NAME = "stepNextConditionalJob"
        const val STEP_1_NAME = "conditionalJobStep1"
        const val STEP_2_NAME = "conditionalJobStep2"
        const val STEP_3_NAME = "conditionalJobStep3"

        private val log = KotlinLogging.logger {}
    }
}
