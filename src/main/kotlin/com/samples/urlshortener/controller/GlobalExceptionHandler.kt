package com.samples.urlshortener.controller

import com.samples.urlshortener.exception.DuplicateEntityException
import com.samples.urlshortener.exception.ResourceNotFoundException
import com.samples.urlshortener.exception.UrlValidationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(UrlValidationException::class)
    fun handleUrlValidationException(ex: UrlValidationException, request: org.springframework.web.context.request.WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Invalid URL provided: {}", ex.message)
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = ex.message ?: "Invalid URL provided",
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DuplicateEntityException::class)
    fun handleDuplicateEntityException(ex: DuplicateEntityException, request: org.springframework.web.context.request.WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("This Id is already exists: {}", ex.message)
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = HttpStatus.CONFLICT.reasonPhrase,
            message = ex.message ?: "Same entity already exists",
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleEntityNotFoundException(ex: ResourceNotFoundException, request: org.springframework.web.context.request.WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Resource not found: {}", ex.message)
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.reasonPhrase,
            message = ex.message ?: "Short URL not found",
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception, request: org.springframework.web.context.request.WebRequest): ResponseEntity<ErrorResponse> {
        logger.error("Internal server error: {}", ex.message, ex)
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An unexpected error occurred",
            path = request.getDescription(false).replace("uri=", "")
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}