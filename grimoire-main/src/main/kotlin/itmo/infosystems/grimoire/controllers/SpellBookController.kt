package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.models.Spell
import itmo.infosystems.grimoire.services.SpellService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/")
class SpellBookController(private val spellService: SpellService) {

    @GetMapping("/all-spellbook")
    fun getSpellBook(pageable: Pageable): Page<Spell> {
        return spellService.getAllSpells(pageable)
    }

    @GetMapping("/my-spellbook")
    fun getMySpellBook(
        principal: Principal?,
        pageable: Pageable
    ): ResponseEntity<Any> {

        // 1. Проверяем, пришел ли токен
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("error" to "Unauthorized"))
        }

        return try {
            val wizardId = principal.name.toLong()
            val spellBook = spellService.getAvailableSpells(wizardId, pageable)
            ResponseEntity.ok(spellBook)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to e.message))
        }
    }
}