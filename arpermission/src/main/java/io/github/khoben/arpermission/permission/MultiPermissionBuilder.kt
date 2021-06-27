package io.github.khoben.arpermission.permission

class MultiPermissionBuilder {
    var allGranted: () -> Unit = {}
    var denied: (List<String>) -> Unit = {}
    var explained: (List<String>) -> Unit = {}
}