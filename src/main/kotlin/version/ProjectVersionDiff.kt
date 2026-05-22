@file:Suppress("unused")
@file:OptIn(ExperimentalSerializationApi::class)

package at.flauschigesalex.rinth.version

import kotlinx.serialization.ExperimentalSerializationApi

@ConsistentCopyVisibility
data class ProjectVersionDiff internal constructor(
    val v1: ProjectVersion,
    val v2: ProjectVersion,
    private val source: Iterable<ProjectVersion>
) {
    val newer = listOf(v1, v2).latest()
    val older = listOf(v1, v2).initial()
    
    val indexDifference: Int = source.indexDifference(newer, older)
}

fun Iterable<ProjectVersion>.indexDifference(v1: ProjectVersion, v2: ProjectVersion) = this.indexOf(v2) - this.indexOf(v1)