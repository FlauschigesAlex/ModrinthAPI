@file:OptIn(ExperimentalSerializationApi::class)

package at.flauschigesalex.rinth.version.file

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class ProjectVersionFileHashes(
    val sha1: String,
    val sha512: String,
)
