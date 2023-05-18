package com.github.zinc.core.proficiency

data class ProficiencyVO(
    var playerId: Long,
    var proficiencyType: String,
    var proficiencyLevel: Int,
    var proficiencyExperience: Int
)