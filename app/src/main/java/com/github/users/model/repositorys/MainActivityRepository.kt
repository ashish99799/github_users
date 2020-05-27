package com.github.users.model.repositorys

import com.github.users.model.api.ApiClient
import com.github.users.model.responses.DataResponse
import io.reactivex.Observable

// Repository
class MainActivityRepository(val api: ApiClient) {
    fun getSearchUser(query: String, page: Int): Observable<DataResponse> {
        // Ratrofit API Calling
        return api.getUserSearch(query, page)
    }
}