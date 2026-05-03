package com.company.organization.rest

import com.company.organization.domain.EmployeeNotFoundException
import com.company.organization.domain.IllegalOrganizationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalOrganizationException::class)
    fun handleIllegalOrganization(ex: IllegalOrganizationException): ResponseEntity<Map<String, String>> =
        badRequest().body(mapOf("error" to (ex.message ?: "Bad request")))

    @ExceptionHandler(EmployeeNotFoundException::class)
    fun handleNotFound(ex: EmployeeNotFoundException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Not found")))
}
