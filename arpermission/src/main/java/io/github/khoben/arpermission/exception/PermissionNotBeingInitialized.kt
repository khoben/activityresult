package io.github.khoben.arpermission.exception

class PermissionNotBeingInitialized : Exception("Call PermissionManager.init() before requesting permissions")