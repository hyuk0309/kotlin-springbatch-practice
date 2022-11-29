package com.elvis.batch.domain

import org.springframework.data.jpa.repository.JpaRepository

interface SalesRepository : JpaRepository<Sales, Long> {
}