package io.github.khoben.arpermission.permission

class ConditionalPermission(
    permission: String,
    val condition: Boolean
) : Permission(permission)