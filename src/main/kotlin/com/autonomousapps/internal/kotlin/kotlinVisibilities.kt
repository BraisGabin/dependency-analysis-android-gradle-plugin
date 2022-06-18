/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 *
 * Copied from https://github.com/JetBrains/kotlin/tree/master/libraries/tools/binary-compatibility-validator
 */

package com.autonomousapps.internal.kotlin

import kotlinx.metadata.Flag
import kotlinx.metadata.Flags
import kotlinx.metadata.jvm.JvmMemberSignature

internal class ClassVisibility(
    val name: String,
    val flags: Flags?,
    val members: Map<JvmMemberSignature, MemberVisibility>,
    val facadeClassName: String? = null
) {
  val visibility get() = flags
  val isCompanion: Boolean get() = flags != null && Flag.Class.IS_COMPANION_OBJECT(flags)

  var companionVisibilities: ClassVisibility? = null
  val partVisibilities = mutableListOf<ClassVisibility>()
}

internal fun ClassVisibility.findMember(signature: JvmMemberSignature): MemberVisibility? =
    members[signature] ?: partVisibilities.mapNotNull { it.members[signature] }.firstOrNull()


internal data class MemberVisibility(val member: JvmMemberSignature, val visibility: Flags?, val isReified: Boolean)

private fun isPublic(visibility: Flags?, isPublishedApi: Boolean) =
    visibility == null
        || Flag.IS_PUBLIC(visibility)
        || Flag.IS_PROTECTED(visibility)
        || (isPublishedApi && Flag.IS_INTERNAL(visibility))

internal fun ClassVisibility.isPublic(isPublishedApi: Boolean) = isPublic(visibility, isPublishedApi)
internal fun MemberVisibility.isPublic(isPublishedApi: Boolean) =
    // Assuming isReified implies inline
    !isReified && isPublic(visibility, isPublishedApi)
