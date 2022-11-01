package com.example.kotlinspringbatchpractice.scheduler

import com.example.kotlinspringbatchpractice.job.CustomReaderJobConfig
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.batch.core.JobParameter
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.JobParametersInvalidException
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import javax.batch.operations.JobExecutionAlreadyCompleteException
import javax.batch.operations.JobExecutionIsRunningException
import kotlin.math.log

@Component
class CustomReaderJobScheduler(
    val jobLauncher: JobLauncher,
    val customReaderJobConfig: CustomReaderJobConfig
) {
    private val logger: Log = LogFactory.getLog(CustomReaderJobConfig::class.java)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    @Scheduled(initialDelay = 1000, fixedDelay = 30000)
    fun runJob() {
        val jobConf = hashMapOf<String, JobParameter>()
        jobConf["time"] = JobParameter(dateFormat.format(System.currentTimeMillis()))
        val jobParameters = JobParameters(jobConf)

        try {
            jobLauncher.run(customReaderJobConfig.customReaderJob(), jobParameters)
        } catch (e: JobExecutionAlreadyCompleteException) {
            logger.error(e.localizedMessage)
        } catch (e: JobExecutionIsRunningException) {
            logger.error(e.localizedMessage)
        } catch (e: JobParametersInvalidException) {
            logger.error(e.localizedMessage)
        }
    }
}