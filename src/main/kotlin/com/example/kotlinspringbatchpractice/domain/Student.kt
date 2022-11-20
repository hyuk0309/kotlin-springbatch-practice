package com.example.kotlinspringbatchpractice.domain

import javax.persistence.*

@Entity
class Student(
    val name: String,

    @ManyToOne
    @JoinColumn(name = "teacher_id", foreignKey = ForeignKey(name = "fk_student_teacher"))
    val teacher: Teacher,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) {
}