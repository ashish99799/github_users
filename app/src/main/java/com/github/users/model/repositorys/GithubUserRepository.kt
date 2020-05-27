package com.github.users.model.repositorys

import com.github.users.model.api.ApiClient
import com.github.users.model.responses.UserData
import com.github.users.model.responses.UserRipoData
import io.reactivex.Observable

// Repository
class GithubUserRepository(val api: ApiClient) {

    fun onUserInfo(query: String): Observable<UserData> {
        // Ratrofit API Calling
        return api.getUserInfo(query)
    }

    fun onUserRipo(query: String): Observable<List<UserRipoData>> {
        // Ratrofit API Calling
        return api.getUserRipo(query)
    }
}