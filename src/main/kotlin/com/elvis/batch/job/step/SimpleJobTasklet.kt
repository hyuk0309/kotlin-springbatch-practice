package com.elvis.batch.job.step

import mu.KotlinLogging
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@StepScope
class SimpleJobTasklet : Tasklet {

    private val logger = KotlinLogging.logger {}

    @Value("#{jobParameters[requestDate]}")
    lateinit var requestDate: String

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        logger.info { ">>>>> This is Step1" }
        logger.info { ">>>>> requestDate = {$requestDate}" }
        return RepeatStatus.FINISHED
    }
}
