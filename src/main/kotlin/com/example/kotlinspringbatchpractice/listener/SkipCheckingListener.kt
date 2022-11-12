package com.example.kotlinspringbatchpractice.listener

import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.listener.StepExecutionListenerSupport

class SkipCheckingListener : StepExecutionListenerSupport() {
    override fun afterStep(stepExecution: StepExecution): ExitStatus? {
        val exitCode = stepExecution.exitStatus.exitCode
        return if (exitCode != ExitStatus.FAILED.exitCode && stepExecution.skipCount > 0) {
            ExitStatus("COMPLETED WITH SKIPS")
        } else {
            null
        }
    }
}