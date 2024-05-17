package com.teamsparta.todoapp.domain.comment.service

import com.teamsparta.todoapp.domain.comment.dto.CommentResponse
import com.teamsparta.todoapp.domain.comment.dto.CreateCommentRequest
import com.teamsparta.todoapp.domain.comment.dto.DeleteCommentRequest
import com.teamsparta.todoapp.domain.comment.dto.UpdateCommentRequest
import com.teamsparta.todoapp.domain.comment.model.Comment
import com.teamsparta.todoapp.domain.comment.model.toResponse
import com.teamsparta.todoapp.domain.comment.repository.CommentRepository
import com.teamsparta.todoapp.domain.exception.ModelNotFoundException
import com.teamsparta.todoapp.domain.todo.repository.TodoRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val todoRepository: TodoRepository,
): CommentService {
    override fun getAllComment(todoId: Long)
            : List<CommentResponse>{
        val comments = commentRepository.findAllByTodoId(todoId)
        return comments.map { it.toResponse() }
    }

    @Transactional
    override fun createComment(todoId: Long,request: CreateCommentRequest): CommentResponse {
        val todo=todoRepository.findByIdOrNull(todoId)
            ?: throw ModelNotFoundException("Todo", todoId)

        val comment=Comment(
            writer = request.writer,
            password = request.passWord,
            comment = request.comment,
            todo = todo
        )

        commentRepository.save(comment)
        return comment.toResponse()
    }

    override fun updateComment(commentId:Long, request: UpdateCommentRequest): CommentResponse {
        val comment= commentRepository.findByIdOrNull(commentId)
            ?: throw ModelNotFoundException("Comment", commentId)
        if(request.writer != comment.writer
            || request.passWord!=comment.password){
            throw ModelNotFoundException("writer.password", commentId)
        }

        comment.comment = request.comment
        return commentRepository.save(comment).toResponse()
    }

    override fun deleteComment(commentId: Long,request: DeleteCommentRequest) {
        val comment= commentRepository.findByIdOrNull(commentId)
            ?: throw ModelNotFoundException("Comment", commentId)
        if(request.writer != comment.writer
            || request.passWord!=comment.password){
            throw ModelNotFoundException("writer.password", commentId)
        }

        commentRepository.delete(comment)
    }
}