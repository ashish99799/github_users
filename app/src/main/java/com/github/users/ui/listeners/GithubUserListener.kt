package com.github.users.ui.listeners

import com.github.users.model.responses.UserData
import com.github.users.model.responses.UserRipoData

interface GithubUserListener {
    fun showProgress()
    fun hideProgress()
    fun onSuccess(data: List<UserRipoData>)
    fun onSuccess(data: UserData)
    fun onFailure(message: String)
}