package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.models.Spell
import itmo.infosystems.grimoire.services.SpellService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
            @AuthenticationPrincipal wizardId: String,
            pageable: Pageable
    ): Page<Spell> {
        return spellService.getAvailableSpells(wizardId.toLong(), pageable)
    }

}