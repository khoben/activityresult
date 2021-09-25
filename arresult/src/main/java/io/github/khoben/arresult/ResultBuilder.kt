package io.github.khoben.arresult

data class ResultBuilder<I>(
    /**
     * Result loaded
     */
    var success: (result: I) -> Unit = {},
    /**
     * Not loaded: Cancelled or Failed
     */
    var failed: (cause: Throwable) -> Unit = {}
)
