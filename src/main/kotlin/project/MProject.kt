@file:OptIn(ExperimentalSerializationApi::class)

package at.flauschigesalex.rinth.project

import at.flauschigesalex.rinth.api.ModrinthVersionAPI
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
data class MProject(
    val slug: String,
    val title: String,
    val description: String,
    val downloads: Int,
    val followers: Int,
    @SerialName("color") val colorRGB: Int,
    val categories: Set<String>,
) {
    companion object;
    
    suspend fun getVersions(loaders: Set<String>? = null, versions: Set<String>? = null) = ModrinthVersionAPI.findAll(slug, loaders, versions)
}