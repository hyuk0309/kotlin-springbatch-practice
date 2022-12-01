package com.elvis.batch.job.stepflowjob

import com.elvis.batch.job.stepflowjob.listener.SkipCheckingListener
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
class StepNextConditionalJob2Configuration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val skipCheckingListener: SkipCheckingListener
) {
    @Bean
    fun stepNextConditionalJob2(): Job {
        return jobBuilderFactory.get(JOB_NAME)
            .start(conditionalJob2Step1())
            .on("FAILED")
            .end()
            .from(conditionalJob2Step1())
            .on("COMPLETED WITH SKIPS")
            .to(conditionalJob2Step3())
            .on("*")
            .end()
            .from(conditionalJob2Step1())
            .on("*")
            .to(conditionalJob2Step2())
            .next(conditionalJob2Step3())
            .on("*")
            .end()
            .end()
            .build()
    }

    @Bean
    fun conditionalJob2Step1(): Step {
        return stepBuilderFactory.get(STEP_1_NAME)
            .tasklet { contribution, _ ->
                log.info { ">>>>> This is $JOB_NAME Step1" }

                contribution.incrementReadSkipCount()

                RepeatStatus.FINISHED
            }
            .listener(skipCheckingListener)
            .build()
    }

    @Bean
    fun conditionalJob2Step2(): Step {
        return stepBuilderFactory.get(STEP_2_NAME)
            .tasklet { _, _ ->
                log.info { ">>>>> This is $JOB_NAME Step2" }
                RepeatStatus.FINISHED
            }
            .build()
    }

    @Bean
    fun conditionalJob2Step3(): Step {
        return stepBuilderFactory.get(STEP_3_NAME)
            .tasklet { _, _ ->
                log.info { ">>>>> This is $JOB_NAME Step3" }
                RepeatStatus.FINISHED
            }
            .build()
    }

    companion object {
        const val JOB_NAME = "stepNextConditionalJob2"
        const val STEP_1_NAME = "conditionalJob2Step1"
        const val STEP_2_NAME = "conditionalJob2Step2"
        const val STEP_3_NAME = "conditionalJob2Step3"

        private val log = KotlinLogging.logger {}
    }
}
