package com.elvis.batch.job

import com.elvis.batch.domain.SalesSum
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ofPattern
import javax.persistence.EntityManagerFactory


@Configuration
class BatchJpaUnitTestConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val emf: EntityManagerFactory
) {

    @Bean
    fun batchJpaUnitTestJob(): Job {
        return jobBuilderFactory.get(JOB_NAME)
            .start(batchJpaUnitTestJobStep())
            .build()
    }

    @Bean
    fun batchJpaUnitTestJobStep(): Step {
        return stepBuilderFactory.get("batchJpaUnitTestJobStep")
            .chunk<SalesSum, SalesSum>(CHUNK_SIZE)
            .reader(batchJpaUnitTestJobReader(null))
            .writer(batchJpaUnitTestJobWriter())
            .build()
    }

    @Bean
    @StepScope
    fun batchJpaUnitTestJobReader(@Value("#{jobParameters[orderDate]}") orderDate: String?): JpaPagingItemReader<SalesSum> {
        val params: Map<String, Any> = hashMapOf(Pair("orderDate", LocalDate.parse(orderDate, FORMATTER)))

        val className = SalesSum::class.java.name
        val queryString = "SELECT new ${className}(s.orderDate, SUM(s.amount)) " +
                "FROM Sales s " +
                "WHERE s.orderDate =:orderDate " +
                "GROUP BY s.orderDate "

        return JpaPagingItemReaderBuilder<SalesSum>()
            .name("batchJpaUnitTestJobReader")
            .entityManagerFactory(emf)
            .pageSize(CHUNK_SIZE)
            .queryString(queryString)
            .parameterValues(params)
            .build()
    }

    @Bean
    fun batchJpaUnitTestJobWriter(): JpaItemWriter<SalesSum> {
        val jpaItemWriter = JpaItemWriter<SalesSum>()
        jpaItemWriter.setEntityManagerFactory(emf)
        return jpaItemWriter
    }

    companion object {
        val FORMATTER = ofPattern("yyyy-MM-dd")

        const val JOB_NAME = "batchJpaUnitTestJob"
        const val CHUNK_SIZE = 10
    }
}
