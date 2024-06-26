package com.teamsparta.todoapp.domain.user.repository

import com.teamsparta.todoapp.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByUserEmail(userEmail: String): User?
    fun existsByUserEmail(userEmail: String): Boolean
}