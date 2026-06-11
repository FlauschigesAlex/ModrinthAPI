@file:Suppress("unused")
@file:OptIn(ExperimentalSerializationApi::class)

package at.flauschigesalex.rinth.project.version

import kotlinx.serialization.ExperimentalSerializationApi

@ConsistentCopyVisibility
data class MProjectVersionDifference internal constructor(
    val v1: MProjectVersion,
    val v2: MProjectVersion,
    private val source: Iterable<MProjectVersion>
) {
    val newer = listOf(v1, v2).latest()
    val older = listOf(v1, v2).initial()
    
    val indexDifference: Int = source.indexDifference(newer, older)
}

fun Iterable<MProjectVersion>.indexDifference(v1: MProjectVersion, v2: MProjectVersion) = this.indexOf(v2) - this.indexOf(v1)
fun MProjectVersionDifference?.onChanges(invocation: MProjectVersionDifference.() -> Unit) {
    if (this == null) return
    if (this.indexDifference <= 0) return
    
    this.invocation()
}