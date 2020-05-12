package com.github.users.model

import com.github.users.model.responses.DataResponse
import com.github.users.model.responses.UserData
import com.github.users.model.responses.UserRipoData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClient {

    companion object {
        operator fun invoke(): ApiClient {
            return Retrofit.Builder()
                .baseUrl("https://api.github.com/") // API Root path
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient::class.java)
        }
    }

    @GET("search/users")
    fun getUserSearch(@Query("q") query: String, @Query("page") page: Int): Call<DataResponse>

    @GET("users/{user}")
    fun getUserInfo(@Path("user") user_name: String): Call<UserData>

    @GET("users/{user}/repos")
    fun getUserRipo(@Path("user") user_name: String): Call<List<UserRipoData>>

}
