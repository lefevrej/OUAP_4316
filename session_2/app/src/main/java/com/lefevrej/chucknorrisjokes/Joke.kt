package com.lefevrej.chucknorrisjokes

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Joke(
    val categories: List<String>,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("icon_url")
    val iconUrl: String,
    val id: String,
    @SerialName("updated_at") val updatedAt: String,
    val url: String, val value: String
)
