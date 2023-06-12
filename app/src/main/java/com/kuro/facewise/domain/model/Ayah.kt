package com.kuro.facewise.domain.model

data class Ayah(
    val ayahId: String,
    val ayahArabic: String,
    val ayahEnglish: String,
    val ayahReference: String
) {
    constructor(): this("", "", "", "")
}
