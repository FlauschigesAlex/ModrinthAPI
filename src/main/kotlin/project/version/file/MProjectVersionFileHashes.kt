@file:OptIn(ExperimentalSerializationApi::class)

package at.flauschigesalex.rinth.project.version.file

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
@ConsistentCopyVisibility
data class MProjectVersionFileHashes internal constructor(
    val sha1: String,
    val sha512: String,
)