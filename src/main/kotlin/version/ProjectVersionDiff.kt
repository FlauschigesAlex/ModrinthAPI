@file:Suppress("unused")
@file:OptIn(ExperimentalSerializationApi::class)

package at.flauschigesalex.rinth.version

import kotlinx.serialization.ExperimentalSerializationApi

data class ProjectVersionDiff(
    val newer: ProjectVersion,
    val older: ProjectVersion,
    private val source: Iterable<ProjectVersion>
) {
    val indexDifference: Int = source.indexDifference(newer, older)
}

fun Iterable<ProjectVersion>.indexDifference(v1: ProjectVersion, v2: ProjectVersion) = this.indexOf(v2) - this.indexOf(v1)