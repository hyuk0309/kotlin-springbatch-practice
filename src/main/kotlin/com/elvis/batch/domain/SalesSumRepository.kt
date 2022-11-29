package com.elvis.batch.domain

import org.springframework.data.jpa.repository.JpaRepository

interface SalesSumRepository : JpaRepository<SalesSum, Long> {
}