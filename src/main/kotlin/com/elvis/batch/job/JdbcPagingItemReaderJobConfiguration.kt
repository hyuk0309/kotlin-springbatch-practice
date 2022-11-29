package com.elvis.batch.job

import com.example.batch.domain.Pay
import mu.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.Order
import org.springframework.batch.item.database.PagingQueryProvider
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import javax.sql.DataSource

@Configuration
class JdbcPagingItemReaderJobConfiguration(
    private val jobBuilderFactory: JobBuilderFactory,
    private val stepBuilderFactory: StepBuilderFactory,
    private val dataSource: DataSource
) {

    companion object {
        const val CHUNK_SIZE = 10
    }

    private val logger = KotlinLogging.logger {}

    @Bean
    fun jdbcPagingItemReaderJob(): Job {
        return jobBuilderFactory.get("jdbcPagingItemReaderJob")
            .start(jdbcPagingItemReaderStep())
            .build()
    }

    @Bean
    fun jdbcPagingItemReaderStep(): Step {
        return stepBuilderFactory.get("jdbcPagingItemReaderStep")
            .chunk<Pay, Pay>(CHUNK_SIZE)
            .reader(jdbcPagingItemReader())
            .writer { list ->
                for (pay in list) {
                    logger.info("Current Pay={$pay}")
                }
            }
            .build()
    }

    @Bean
    fun jdbcPagingItemReader(): JdbcPagingItemReader<Pay> {
        val parameterValues = hashMapOf<String, Any>()
        parameterValues["amount"] = 2000

        return JdbcPagingItemReaderBuilder<Pay>()
            .pageSize(CHUNK_SIZE)
            .fetchSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(Pay::class.java))
            .queryProvider(createQueryProvider())
            .parameterValues(parameterValues)
            .name("jdbcPagingItemReader")
            .build()
    }

    @Bean
    fun createQueryProvider(): PagingQueryProvider {
        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setDataSource(dataSource)
        queryProvider.setSelectClause("id, amount, tx_name, tx_date_time")
        queryProvider.setFromClause("from pay")
        queryProvider.setWhereClause("where amount >= :amount")

        val sortKeys = hashMapOf<String, Order>()
        sortKeys["id"] = Order.ASCENDING

        queryProvider.setSortKeys(sortKeys)

        return queryProvider.`object`
    }
}