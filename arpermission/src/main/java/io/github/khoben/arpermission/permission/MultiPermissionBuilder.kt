package io.github.khoben.arpermission.permission

internal class MultiPermissionBuilder {
    var allGranted: () -> Unit = {}
    var denied: (permissions: List<String>, isCancelled: Boolean) -> Unit = { _, _ -> }
    var explained: (permissions: List<String>) -> Unit = {}
    var requestFinished: () -> Unit = {}
}