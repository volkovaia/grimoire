package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.dto.requests.SpellCastRequest
import itmo.infosystems.grimoire.dto.responses.SpellCastResponse
import itmo.infosystems.grimoire.models.SpellCast
import itmo.infosystems.grimoire.services.SpellCastService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal

@RestController
@RequestMapping("/temlates/spells")
class SpellCastController(private val spellCastService: SpellCastService) {

//    @PostMapping("/cast")
//    fun castSpell(
//        @AuthenticationPrincipal principal: Principal,
//        @RequestBody request: SpellCastRequest
//    ): SpellCast {
//        return spellCastService.castSpell(principal.name.toLong(), request)
//    }

    @PostMapping("/cast")
    fun castSpell(principal: Principal?, @RequestBody request: SpellCastRequest): ResponseEntity<SpellCastResponse> {
        // 1. ИСПРАВЛЕНИЕ ОШИБКИ 500: Проверка Principal на null
        val wizardId = principal?.name?.toLong()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated to cast spells")

        // 2. Вызов сервиса для выполнения логики
        // Предполагается, что spellCastService.castSpell возвращает сущность SpellCast
        val newSpellCast: SpellCast = spellCastService.castSpell(wizardId, request)

        // 3. Преобразование сущности SpellCast в SpellCastResponse DTO
        val response = SpellCastResponse(
                castId = newSpellCast.id,
                status = newSpellCast.status.name, // Преобразование ENUM в String
                castTime = newSpellCast.castTime,
                expireTime = newSpellCast.expireTime,
                spellId = newSpellCast.spell.id,
                spellName = newSpellCast.spell.name, // Предполагается, что Spell имеет поле 'name'
                victimId = newSpellCast.victim.id,
                victimName = newSpellCast.victim.name // Предполагается, что Human имеет поле 'name'
        )

        // 4. Возврат успешного ответа
        return ResponseEntity.ok(response)
    }

    @PutMapping("/remove/{id}")
    fun removeSpell(@PathVariable id: Long, @AuthenticationPrincipal principal: Principal): SpellCast {
        return spellCastService.removeSpell(principal.name.toLong(), id)
    }

//    @GetMapping("/active/mine")
//    fun getMyActiveSpells(@AuthenticationPrincipal principal: Principal): List<SpellCast> {
//        return spellCastService.getActiveSpells(principal.name.toLong())
//    }

    @GetMapping("/active/mine")
    fun getMyActiveSpells(@AuthenticationPrincipal principal: Principal?): List<SpellCast> { // <-- ИСПРАВЛЕНО
        val wizardId = principal?.name?.toLong()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated")
        return spellCastService.getActiveSpells(wizardId)
    }

//    @GetMapping("/active/others")
//    fun getOthersActiveSpells(@AuthenticationPrincipal principal: Principal): List<SpellCast> {
//        return spellCastService.getActiveSpellsFromWizardsWithLowerOrEqualGuildLevel(principal.name.toLong())
//    }
    @GetMapping("/active/others")
    fun getOthersActiveSpells(@AuthenticationPrincipal principal: Principal?): List<SpellCast> { // <-- ИСПРАВЛЕНО
        val wizardId = principal?.name?.toLong()
                ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User must be authenticated")
        return spellCastService.getActiveSpellsFromWizardsWithLowerOrEqualGuildLevel(wizardId)
    }



}
