// SpellCastResponse.kt
package itmo.infosystems.grimoire.dto.responses

import java.time.LocalDateTime

data class SpellCastResponse(
        // Основные данные о наложении
        val castId: Long,
        val status: String,
        val castTime: LocalDateTime,
        val expireTime: LocalDateTime?,

        // Данные о Заклинании
        val spellId: Long,
        val spellName: String,

        // Данные о Жертве (Human)
        val victimId: Long,
        val victimName: String // Предполагается, что в Human есть поле 'name'
)