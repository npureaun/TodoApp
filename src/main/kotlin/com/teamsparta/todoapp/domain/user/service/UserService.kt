package com.teamsparta.todoapp.domain.user.service

import com.teamsparta.todoapp.domain.user.dto.LogInUserRequest
import com.teamsparta.todoapp.domain.user.dto.LoginResponse
import com.teamsparta.todoapp.domain.user.dto.SignUpUserRequest
import com.teamsparta.todoapp.domain.user.dto.UserResponse
import com.teamsparta.todoapp.domain.user.model.Profile
import com.teamsparta.todoapp.domain.user.model.User
import com.teamsparta.todoapp.domain.user.model.UserRole
import com.teamsparta.todoapp.domain.user.model.toResponse
import com.teamsparta.todoapp.domain.user.repository.UserRepository
import com.teamsparta.todoapp.infra.security.jwt.JwtUtil
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.naming.AuthenticationException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
){

    @Transactional
    fun signUpUser(request: SignUpUserRequest):UserResponse {
        if (userRepository.existsByUserEmail(request.userEmail)) {
            throw IllegalStateException("Email is already in use")
        }

        return userRepository.save(
            User(
                userEmail = request.userEmail,
                userPassword = passwordEncoder.encode(request.userPassword),
                profile = Profile(nickname = request.nickname),
                role = when (request.role) {
                    UserRole.STUDENT.name -> UserRole.STUDENT
                    UserRole.TUTOR.name -> UserRole.TUTOR
                    else -> throw IllegalArgumentException("Invalid role")
                }
            )
        ).toResponse()
    }

    @Transactional
    fun logInUser(request: LogInUserRequest):LoginResponse {
        val user = userRepository.findByUserEmail(request.userEmail)
            ?: throw EntityNotFoundException("User Not Found")
        if (user.role.name != request.role
            || !passwordEncoder.matches(request.userPassword, user.userPassword))
            throw AuthenticationException("User Info Not Match")
        return LoginResponse(
            accessToken = jwtUtil.generateAccessToken(
                subject = user.id.toString(),
                email = user.userEmail,
                role = user.role.name
            ))
    }

    @Transactional
    fun getUserInfo():UserResponse{
        val authentication = SecurityContextHolder.getContext().authentication
        val principalString = authentication.principal.toString()
        val emailRegex = """email=([^,]+)""".toRegex()
        val matchResult = emailRegex.find(principalString)
        val userEmail = matchResult?.groups?.get(1)?.value
            ?: throw EntityNotFoundException("User email not found in Token")
        return userRepository.findByUserEmail(userEmail)?.toResponse()
            ?: throw EntityNotFoundException("User Not Found")
    }
}