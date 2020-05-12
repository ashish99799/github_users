package com.github.users.model.responses

data class Errors(
    var resource: String? = "",
    var code: String? = "",
    var field: String? = ""
)