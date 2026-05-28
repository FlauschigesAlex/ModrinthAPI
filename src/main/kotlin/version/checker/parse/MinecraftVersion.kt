package at.flauschigesalex.rinth.version.checker.parse

@ConsistentCopyVisibility
data class MinecraftVersion private constructor(val raw: String) {
    companion object {
        fun of(raw: String) = MinecraftVersion(raw)
        fun of(vararg raw: String) = raw.map(::of).toSet()
    }
}