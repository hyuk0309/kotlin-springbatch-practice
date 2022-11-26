package com.example.kotlinspringbatchpractice.job

import com.example.kotlinspringbatchpractice.domain.Sales
import com.example.kotlinspringbatchpractice.domain.SalesRepository
import com.example.kotlinspringbatchpractice.domain.SalesSum
import com.example.kotlinspringbatchpractice.domain.SalesSumRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.StepExecution
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.database.JpaPagingItemReader
import org.springframework.batch.test.MetaDataInstanceFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import java.time.LocalDate

@SpringBootTest
internal class BatchJpaUnitTestConfigurationTest {

    @Autowired
    private lateinit var reader: JpaPagingItemReader<SalesSum>

    @Autowired
    private lateinit var salesRepository: SalesRepository

    @Autowired
    private lateinit var salesSumRepository: SalesSumRepository

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @After
    fun tearDown() {
        salesRepository.deleteAllInBatch()
        salesSumRepository.deleteAllInBatch()
    }

    fun getStepExecution(): StepExecution {
        val jobParameters = JobParametersBuilder()
            .addString("orderDate", orderDate.format(BatchJpaUnitTestConfiguration.FORMATTER))
            .toJobParameters()

        return MetaDataInstanceFactory.createStepExecution(jobParameters)
    }

    @Test
    fun 기간내_Sales가_집계되어_SalesSum이된다() {
        //given
        val amount1 = 1000L
        val amount2 = 500L
        val amount3 = 100L

        saveSales(amount1, "1")
        saveSales(amount2, "2")
        saveSales(amount3, "3")

        reader.open(ExecutionContext())

        //when & then
        assertThat(reader.read()!!.amountSum).isEqualTo(amount1 + amount2 + amount3)
        assertThat(reader.read()).isNull()
    }

    private fun saveSales(amount: Long, orderNo: String): Sales {
        return salesRepository.save(Sales(orderDate, amount, orderNo))
    }

    companion object {
        val orderDate: LocalDate = LocalDate.of(2022, 11, 26)
    }
}
