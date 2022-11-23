package com.example.kotlinspringbatchpractice.domain

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class SalesSum(
    val orderDate: LocalDate,
    val amountSum: Long,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null
) {
}