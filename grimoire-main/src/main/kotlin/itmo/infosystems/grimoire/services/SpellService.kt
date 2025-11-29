package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.models.Spell
import itmo.infosystems.grimoire.repositories.SpellRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SpellService(private val spellRepository: SpellRepository) {
    fun getAvailableSpells(wizardId: Long, pageable: Pageable): Page<Spell> =
            spellRepository.findAvailableSpellsByWizardId(wizardId, pageable)

    fun getAllSpells(pageable: Pageable): Page<Spell> =
            spellRepository.findAll(pageable)

}