package at.flauschigesalex.rinth.version

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ProjectVersionType.Companion.ProjectVersionTypeSerializer::class)
enum class ProjectVersionType {
    RELEASE, BETA, ALPHA,;
    companion object {
        fun find(name: String) = entries.find { it.name.equals(name, true) }
        
        internal object ProjectVersionTypeSerializer : KSerializer<ProjectVersionType> {
            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("ProjectVersionType", PrimitiveKind.STRING)
            override fun serialize(encoder: Encoder, value: ProjectVersionType) = encoder.encodeString(value.name)
            override fun deserialize(decoder: Decoder): ProjectVersionType = find(decoder.decodeString()) ?: RELEASE
        }
    }
}