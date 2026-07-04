package at.flauschigesalex.rinth.utils.serialize

import at.flauschigesalex.lib.base.general.version.SemanticVersion
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object SemanticVersionSerializer: KSerializer<SemanticVersion> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SemanticVersion", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: SemanticVersion) = encoder.encodeString(value.version)
    override fun deserialize(decoder: Decoder): SemanticVersion = SemanticVersion.parseOrThrow(decoder.decodeString())
}

typealias SemanticVersionSerialized = @Serializable(SemanticVersionSerializer::class) SemanticVersion