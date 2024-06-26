package com.teamsparta.todoapp.domain.todo.service

import com.teamsparta.todoapp.domain.todo.dto.comment.CommentResponse
import com.teamsparta.todoapp.domain.todo.dto.comment.CreateCommentRequest
import com.teamsparta.todoapp.domain.todo.dto.comment.UpdateCommentRequest
import com.teamsparta.todoapp.domain.todo.model.Comment
import com.teamsparta.todoapp.domain.todo.model.toResponse
import com.teamsparta.todoapp.domain.todo.repository.comment.CommentRepository
import com.teamsparta.todoapp.domain.user.service.UserService
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val userService: UserService,
    private val commentRepository: CommentRepository,
) {
    private fun userChecking(comparison:String) {
        userService.getUserInfo()
            .let {
                if (it.userEmail != comparison) {
                    throw IllegalArgumentException("User Not Match")
                }
            }
    }

    private fun getCommentEntity(commentId : Long): Comment {
        return commentRepository.findByIdOrNull(commentId)
            ?: throw EntityNotFoundException("Comment $commentId not found")
    }

    @Transactional
    fun createComment(todoId: Long, request: CreateCommentRequest)
    : CommentResponse {
        return commentRepository
            .save(Comment.saveEntity(todoId,request,userService.getUserInfo()))
            .toResponse()
    }

    @Transactional
    fun updateComment(commentId: Long, request: UpdateCommentRequest)
    : CommentResponse {
        val comment = getCommentEntity(commentId)
        userChecking(comment.userEmail)
        comment.comment = request.comment
        return comment.toResponse()
    }

    @Transactional
    fun deleteComment(commentId: Long) {
        val comment = getCommentEntity(commentId)
        userChecking(comment.userEmail)
        commentRepository.delete(comment)
    }
}