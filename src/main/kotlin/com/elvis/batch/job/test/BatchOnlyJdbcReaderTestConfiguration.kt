package com.elvis.batch.job.test

import com.elvis.batch.domain.SalesSum
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import java.time.format.DateTimeFormatter.ofPattern
import javax.sql.DataSource

@Configuration
class BatchOnlyJdbcReaderTestConfiguration(
    private val dataSource: DataSource
) {

    companion object {
        val FORMATTER = ofPattern("yyyy-MM-dd")
        private const val JOB_NAME = "batchOnlyJdbcReaderTestJob"
        private const val CHUNK_SIZE = 10
    }

    @Bean
    fun batchOnlyJdbcReaderTestJobReader(): JdbcPagingItemReader<SalesSum> {
        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setDataSource(dataSource)
        queryProvider.setSelectClause("order_date, sum(amount) as amount_sum")
        queryProvider.setFromClause("from sales")
        queryProvider.setGroupClause("group by order_date")
        queryProvider.setSortKey("order_date")

        return JdbcPagingItemReaderBuilder<SalesSum>()
            .name("batchOnlyJdbcReaderTestJobReader")
            .pageSize(CHUNK_SIZE)
            .fetchSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(SalesSum::class.java))
            .queryProvider(queryProvider.`object`)
            .build()
    }
}
