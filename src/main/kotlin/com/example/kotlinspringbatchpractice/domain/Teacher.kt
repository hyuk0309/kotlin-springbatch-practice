package com.example.kotlinspringbatchpractice.domain

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class Teacher(
    val name: String,

    val subject: String,

    @OneToMany(mappedBy = "teacher", cascade = [CascadeType.ALL])
    val students: List<Student>? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) {
}