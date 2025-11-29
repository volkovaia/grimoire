package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.dto.requests.RegisterRequest
import itmo.infosystems.grimoire.dto.requests.LoginRequest
import itmo.infosystems.grimoire.dto.responses.AuthResponse
import itmo.infosystems.grimoire.services.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): AuthResponse {
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse {
        return authService.login(request)
    }
//@PostMapping("/login")
//fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
//    val token = authService.authenticate(request.login, request.password)
//
//    // Получаем данные мага из базы по логину
//    val wizard = wizardService.findByLogin(request.login)
//            ?: return ResponseEntity.badRequest().body(LoginResponse(null, null))
//
//    return ResponseEntity.ok(LoginResponse(token, wizard))
//}
//
//    data class LoginResponse(
//            val token: String?,
//            val wizard: Wizard? // ← вот это поле должно быть!
//    )
//
//    data class Wizard(
//            val id: Long,
//            val login: String,
//            val name: String,
//            val surname: String
//    )
}