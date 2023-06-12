package com.kuro.facewise.domain.model

data class Hadith(
    val hadithId: String,
    val hadith: String,
    val hadithReference: String
) {
    constructor(): this("","","")
}
