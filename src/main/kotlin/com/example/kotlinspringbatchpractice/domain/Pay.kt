package com.example.kotlinspringbatchpractice.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Pay(
    val amount: Long,
    val txName: String,
    val txDateTime: LocalDateTime,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)