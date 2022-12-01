package com.elvis.batch.job.stepflowjob

import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.Step
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.job.flow.FlowExecutionStatus
import org.springframework.batch.core.job.flow.JobExecutionDecider
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class DeciderJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory
) {
    @Bean
    fun deciderJob(): Job {
        return jobBuilderFactory.get(JOB_NAME)
            .start(startStep())
            .next(decider())
            .from(decider())
            .on("ODD")
            .to(oddStep())
            .from(decider())
            .on("EVEN")
            .to(evenStep())
            .end()
            .build()
    }

    @Bean
    fun startStep(): Step {
        return stepBuilderFactory.get(START_STEP_NAME)
            .tasklet { _, _ ->
                logger.info { ">>>>> Start!" }
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun evenStep(): Step {
        return stepBuilderFactory.get(EVEN_STEP_NAME)
            .tasklet { _, _ ->
                logger.info { ">>>>> 짝수입니다." }
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun oddStep(): Step {
        return stepBuilderFactory.get(ODD_STEP_NAME)
            .tasklet { _, _ ->
                logger.info { ">>>>> 홀수입니다." }
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun decider(): JobExecutionDecider {
        return OddDecider()
    }

    class OddDecider : JobExecutionDecider {

        companion object {
            private val log = KotlinLogging.logger {}
        }

        override fun decide(jobExecution: JobExecution, stepExecution: StepExecution?): FlowExecutionStatus {
            val rand = Random()

            val randomNumber = rand.nextInt(50) + 1
            log.info { "랜덤숫자: {$randomNumber}" }

            return if (randomNumber % 2 == 0) {
                FlowExecutionStatus("EVEN")
            } else {
                FlowExecutionStatus("ODD")
            }
        }
    }

    companion object {
        const val JOB_NAME = "deciderJob"
        const val START_STEP_NAME = "startStep"
        const val EVEN_STEP_NAME = "evenStep"
        const val ODD_STEP_NAME = "oddStep"

        private val logger = KotlinLogging.logger {}
    }
}
