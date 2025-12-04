package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Spell
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface SpellRepository : JpaRepository<Spell, Long> {

    companion object {
        private const val FIND_SPELL_BOOK_QUERY = """
            SELECT s.*
            FROM spell s
            CROSS JOIN (
                SELECT g.level
                FROM wizard w
                JOIN guild g ON w.guild_id = g.guild_id
                WHERE w.wizard_id = :wizardId
            ) wizard_info
            WHERE s.required_guild_level <= wizard_info.level
        """

        private const val FIND_SPELL_BOOK_COUNT_QUERY = """
            SELECT COUNT(*)
            FROM spell s
            CROSS JOIN (
                SELECT g.level
                FROM wizard w
                JOIN guild g ON w.guild_id = g.guild_id
                WHERE w.wizard_id = :wizardId
            ) wizard_info
            WHERE s.required_guild_level <= wizard_info.level
        """
    }

    @Query(value = FIND_SPELL_BOOK_QUERY, countQuery = FIND_SPELL_BOOK_COUNT_QUERY, nativeQuery = true)
    fun findAvailableSpellsByWizardId(@Param("wizardId") wizardId: Long, pageable: Pageable): Page<Spell>


}