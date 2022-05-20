package io.github.khoben.arresult

class ResultBuilder<I>(
    /**
     * Result loaded
     */
    var success: (result: I) -> Unit = {},
    /**
     * Not loaded: Cancelled or Failed
     */
    var failed: (cause: Throwable) -> Unit = {}
)
