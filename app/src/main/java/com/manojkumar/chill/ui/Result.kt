package com.manojkumar.chill.ui

data class Result(
    var loading: Boolean = false,
    var success: Boolean = false,
    var failure: Boolean = false,
    var message: String? = null,
    var data: Any? = null
)