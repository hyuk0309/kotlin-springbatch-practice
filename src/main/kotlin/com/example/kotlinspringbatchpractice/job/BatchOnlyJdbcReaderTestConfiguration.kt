package com.example.kotlinspringbatchpractice.job

import com.example.kotlinspringbatchpractice.domain.SalesSum
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.database.JdbcPagingItemReader
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.BeanPropertyRowMapper
import java.time.LocalDate
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
    @StepScope
    fun batchOnlyJdbcReaderTestJobReader(@Value("#{jobParameter[orderDate]}") orderDate: String): JdbcPagingItemReader<SalesSum> {
        val params: Map<String, Any> = hashMapOf()

        params.plus(Pair("orderDate", LocalDate.parse(orderDate, FORMATTER)))

        val queryProvider = SqlPagingQueryProviderFactoryBean()
        queryProvider.setDataSource(dataSource)
        queryProvider.setSelectClause("order_date, sum(amount) as amount_sum")
        queryProvider.setFromClause("from sales")
        queryProvider.setWhereClause("where order_date =:orderDate")
        queryProvider.setGroupClause("group by order_date")
        queryProvider.setSortKey("order_date")

        return JdbcPagingItemReaderBuilder<SalesSum>()
            .name("batchOnlyJdbcReaderTestJobReader")
            .pageSize(CHUNK_SIZE)
            .fetchSize(CHUNK_SIZE)
            .dataSource(dataSource)
            .rowMapper(BeanPropertyRowMapper(SalesSum::class.java))
            .queryProvider(queryProvider.`object`)
            .parameterValues(params)
            .build()
    }
}
