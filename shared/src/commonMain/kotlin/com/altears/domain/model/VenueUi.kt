package com.altears.domain.model

data class VenueUi(
    val id: Int,
    val name: String,
    val subtitle: String?,
    val showCount: Int = 0
)
