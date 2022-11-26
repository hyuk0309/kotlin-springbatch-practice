package com.example.kotlinspringbatchpractice.domain

import org.springframework.data.jpa.repository.JpaRepository

interface SalesSumRepository : JpaRepository<SalesSum, Long> {
}