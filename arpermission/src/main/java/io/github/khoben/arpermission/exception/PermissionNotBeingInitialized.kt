package io.github.khoben.arpermission.exception

class PermissionNotBeingInitialized :
    IllegalStateException("Requesting permissions before registering them.\nExpected registration with PermissionManager.hasRuntimePermissions().")