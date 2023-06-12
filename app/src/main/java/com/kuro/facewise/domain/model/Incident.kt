package com.kuro.facewise.domain.model

data class Incident(
    val incidentId: String,
    val incident: String,
    val incidentReference: String
) {
    constructor(): this("","","")
}
