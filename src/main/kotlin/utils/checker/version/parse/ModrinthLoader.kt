package at.flauschigesalex.rinth.utils.checker.version.parse

@ConsistentCopyVisibility
data class ModrinthLoader private constructor(val name: String) {
    companion object {
        fun of(loader: String) = ModrinthLoader(loader)
        fun of(vararg loaders: String) = loaders.map(::of).toSet()
    }
}