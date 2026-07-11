package at.flauschigesalex.rinth.project.version

import at.flauschigesalex.lib.base.general.version.VersionType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(MProjectVersionTypeSerializer::class)
@Suppress("unused")
enum class MProjectVersionType {
    RELEASE, BETA, ALPHA,;
    companion object {
        
        fun find(name: String) = entries.find { it.name.equals(name, true) }
    }
}

fun VersionType.toMProjectVersionType() = when (this) {
    VersionType.RELEASE -> MProjectVersionType.RELEASE
    VersionType.BETA, VersionType.RELEASE_CANDIDATE, VersionType.SNAPSHOT -> MProjectVersionType.BETA
    VersionType.ALPHA -> MProjectVersionType.ALPHA
}

internal object MProjectVersionTypeSerializer : KSerializer<MProjectVersionType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("MProjectVersionType", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: MProjectVersionType) = encoder.encodeString(value.name)
    override fun deserialize(decoder: Decoder): MProjectVersionType = decoder.decodeString().let { MProjectVersionType.find(it) }!!
}