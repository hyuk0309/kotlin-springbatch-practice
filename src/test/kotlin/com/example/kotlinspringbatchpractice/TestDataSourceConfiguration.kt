package com.example.kotlinspringbatchpractice

import org.springframework.batch.test.DataSourceInitializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2
import javax.sql.DataSource

@TestConfiguration
class TestDataSourceConfiguration {

    companion object {
        const val CREATE_SQL =
            "create table IF NOT EXISTS `sales` " +
                    "(id bigint not null auto_increment," +
                    " amount bigint not null," +
                    " order_date date," +
                    " order_no varchar(255)," +
                    " primary key (id)) engine=InnoDB;";
    }

    @Bean
    fun dataSource(): DataSource {
        val databaseFactory = EmbeddedDatabaseFactory()
        databaseFactory.setDatabaseType(H2)
        return databaseFactory.database
    }

    @Bean
    fun initializer(dataSource: DataSource): DataSourceInitializer {
        val dataSourceInitializer = DataSourceInitializer()
        dataSourceInitializer.setDataSource(dataSource)

        val create = ByteArrayResource(CREATE_SQL.encodeToByteArray())
        dataSourceInitializer.setInitScripts(arrayOf(ClassPathResource("test-scheme.sql")))

        return dataSourceInitializer
    }
}