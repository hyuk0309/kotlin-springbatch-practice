package com.elvis.batch.domain

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
    val students: MutableList<Student> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) {
}