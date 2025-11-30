package itmo.infosystems.grimoire.exceptions

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: "Invalid input")))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid") }
        return ResponseEntity.badRequest().body(mapOf("errors" to errors))
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNoSuchElement(ex: EntityNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(404).body(mapOf("error" to (ex.message ?: "Element not found")))
    }

    @ExceptionHandler(SpellCastException::class)
    fun handleNoSuchElement(ex: SpellCastException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: "Unknown spell casting error")))
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleNotSupportedType(ex: HttpMediaTypeNotSupportedException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: "Content-Type is not supported")))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknown(ex: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(500).body(mapOf("error" to (ex.message ?: "Internal server error")))
        //return ResponseEntity.status(500).body(mapOf("error" to "Internal server error"))
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<Map<String, String>> {
        // Получаем сообщение. ?: гарантирует, что это не null, чтобы избежать Type Mismatch
        val errorMessage: String = ex.reason ?: "Error processing request"

        // Возвращаем статус и тело ошибки, которое задано в ResponseStatusException (например, 401 и сообщение)
        return ResponseEntity
                .status(ex.statusCode)
                .body(mapOf("error" to errorMessage)) // Используем Kotlin mapOf(key to value)
    }

}