package com.elvis.batch.job.test

import com.elvis.batch.domain.SalesSum
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.database.JdbcBatchItemWriter
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.*
import javax.sql.DataSource

@Configuration
class BatchJdbcUnitTestConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val dataSource: DataSource
) {

    @Bean
    fun batchJdbcUnitTestJob(): Job {
        return jobBuilderFactory.get(JOB_NAME)
            .start(batchJdbcUnitTestJobStep())
            .build()
    }

    @Bean
    fun batchJdbcUnitTestJobStep(): Step {
        return stepBuilderFactory.get("batchJdbcUnitTestJobStep")
            .chunk<SalesSum, SalesSum>(CHUNK_SIZE)
            .reader(batchJdbcUnitTestJobReader(null))
            .writer(batchJdbcUnitTestJobWriter())
            .build()
    }

    @Bean
    @StepScope
    fun batchJdbcUnitTestJobReader(@Value("#{jobParamters[orderDate]}") orderDate: String?): JdbcPagingItemReader<SalesSum> {
        val params: HashMap<String, Any> = hashMapOf(Pair("orderDate", LocalDate.parse(orderDate, FORMATTER)))

        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setDataSource(dataSource)
        queryProvider.setSelectClause("order_date, sum(amount) as amount_sum")
        queryProvider.setFromClause("from sales")
        queryProvider.setWhereClause("where order_date =:orderDate")
        queryProvider.setGroupClause("group by order_date")
        queryProvider.setSortKey("order_date")

        return JdbcPagingItemReaderBuilder<SalesSum>()
            .name("batchJdbcUnitTestJobReader")
            .pageSize(CHUNK_SIZE)
            .fetchSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(SalesSum::class.java))
            .queryProvider(queryProvider.`object`)
            .parameterValues(params)
            .build()
    }

    @Bean
    fun batchJdbcUnitTestJobWriter(): JdbcBatchItemWriter<SalesSum> {
        return JdbcBatchItemWriterBuilder<SalesSum>()
            .dataSource(dataSource)
            .sql("insert into sales_sum(order_date, amount_sum) values (:order_date, :amount_sum)")
            .beanMapped()
            .build()
    }

    companion object {
        val FORMATTER: DateTimeFormatter = ofPattern("yyyy-MM-dd")
        val log = KotlinLogging.logger {}

        const val JOB_NAME = "batchJdbcUnitTestJob"
        const val CHUNK_SIZE = 10
    }
}
