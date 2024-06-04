package com.teamsparta.todoapp.domain.todo.controller

import com.teamsparta.todoapp.domain.exception.CreateUpdateException
import com.teamsparta.todoapp.domain.todo.dto.todo.CreateTodoRequest
import com.teamsparta.todoapp.domain.todo.dto.todo.TodoResponse
import com.teamsparta.todoapp.domain.todo.dto.todo.UpdateTodoRequest
import com.teamsparta.todoapp.domain.todo.service.SortTodoSelector
import com.teamsparta.todoapp.domain.todo.service.TodoService
import jakarta.validation.Valid
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RequestMapping("/todos")
@RestController
class TodoController(private val todoService: TodoService) {

    @GetMapping("/search")
    @PreAuthorize("hasRole('TUTOR') or hasRole('STUDENT')")
    fun searchCourseList(@RequestParam(name = "title") title: String): ResponseEntity<List<TodoResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(todoService.searchCourseList(title))
    }
    @GetMapping()
    @PreAuthorize("hasRole('TUTOR') or hasRole('STUDENT')")
    fun getTodoList(
        @RequestParam(defaultValue = "SUCCESS_ASC_DATE_DESC") sortBy: SortTodoSelector,
        @RequestParam(defaultValue = "") writer:String,
        @RequestParam(defaultValue = "0") page:Int,
    )
    : ResponseEntity<Slice<TodoResponse>>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(todoService.getAllTodoList(sortBy,writer,page))
    }

    @GetMapping("/{todoId}")
    @PreAuthorize("hasRole('TUTOR') or hasRole('STUDENT')")
    fun getTodoById(@PathVariable todoId: Long)
    : ResponseEntity<TodoResponse>{
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(todoService.getTodoById(todoId))
    }

    @PostMapping
    @PreAuthorize("hasRole('TUTOR')")
    fun createTodo(@Valid @RequestBody createTodoRequest: CreateTodoRequest,
                   bindingResult: BindingResult)
    :ResponseEntity<TodoResponse>{
        if(bindingResult.hasErrors())
            throw CreateUpdateException("Create")

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(todoService.createTodo(createTodoRequest))
    }

    @PutMapping("/{todoId}")
    @PreAuthorize("hasRole('TUTOR')")
    fun updateTodo(@PathVariable todoId: Long,
                   @Valid @RequestBody updateTodoRequest: UpdateTodoRequest,
                   bindingResult: BindingResult)
    : ResponseEntity<TodoResponse> {
        if(bindingResult.hasErrors()){ throw CreateUpdateException("Update") }
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(todoService.updateTodo(todoId, updateTodoRequest))
    }

    @PatchMapping("/{todoId}/success")
    @PreAuthorize("hasRole('TUTOR') or hasRole('STUDENT')")
    //@PreAuthorize("hasRole('TUTOR')")
    fun successTodo(@PathVariable todoId: Long)
            : ResponseEntity<TodoResponse> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(todoService.successTodo(todoId))
    }

    @DeleteMapping("/{todoId}")
    @PreAuthorize("hasRole('TUTOR')")
    fun deleteTodo(@PathVariable todoId: Long)
    :ResponseEntity<Unit> {
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(todoService.deleteTodo(todoId))
    }

    @DeleteMapping
    @PreAuthorize("hasRole('TUTOR')")
    fun clearTodos()
            :ResponseEntity<Unit> {
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(todoService.clearTodos())
    }
}