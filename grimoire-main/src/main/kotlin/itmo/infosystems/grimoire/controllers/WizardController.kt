package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.models.Wizard
import itmo.infosystems.grimoire.services.WizardService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/wizards")
class WizardController(private val wizardService: WizardService) {

    @GetMapping("/me")
    fun getMe(principal: Principal?): ResponseEntity<Any> {
        // 1. Проверка на то, что пользователь залогинен
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized"))
        }

        return try {
            // 2. В токене у нас лежит ID (например, "2"), поэтому превращаем строку в Long
            val wizardId = principal.name.toLong()

            // 3. Ищем мага по ID, а не по логину
            val wizard = wizardService.getWizard(wizardId)

            ResponseEntity.ok(wizard)
        } catch (e: NumberFormatException) {
            // Это на случай, если вдруг в токене окажется не число
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Invalid token data"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to e.message))
        }
    }

    @GetMapping("/{id}")
    fun getWizard(@PathVariable id: Long): Wizard {
        return wizardService.getWizard(id)
    }

    @PutMapping("/join/{guildId}")
    fun joinGuild(
        @AuthenticationPrincipal principal: Principal,
        @PathVariable guildId: Long
    ): Wizard {
        val updatedWizard = wizardService.joinGuild(principal.name.toLong(), guildId)
        return wizardService.getWizard(updatedWizard.id)
    }
}