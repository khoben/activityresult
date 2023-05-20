package io.github.khoben.arpermission.permission

open class Permission(
    protected val permission: String
) : Comparable<String> by permission, CharSequence by permission {
    override fun toString(): String {
        return permission
    }
}