package com.github.users.model.responses

data class UserRipoData(
    var name: String? = null,
    var forks: Int? = 0,
    var stargazers_count: Int? = 0
)