package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.services.ArtifactService
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/artifacts")
class ArtifactController(private val artifactService: ArtifactService) {

    @GetMapping
    fun getArtifacts(
        principal: Principal?, // <--- Добавили знак вопроса
        pageable: Pageable
    ): ResponseEntity<Any> {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized"))
        }

        return try {
            // Превращаем ID из строки в число

            val wizardId = principal.name.toLong()

            val inventory = artifactService.getArtifacts(wizardId, pageable)

            ResponseEntity.ok(inventory)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to e.message))
        }
    }
}