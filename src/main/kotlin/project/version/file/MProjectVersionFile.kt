@file:OptIn(ExperimentalSerializationApi::class)

package at.flauschigesalex.rinth.project.version.file

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class MProjectVersionFile(
    val id: String,
    val primary: Boolean,
    val size: Int,
    @SerialName("filename") val name: String,
    val url: String,
    val hashes: MProjectVersionFileHashes,
)