package com.github.users.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.users.model.ApiClient
import com.github.users.model.responses.DataResponse
import com.github.users.model.responses.RowData
import com.github.users.model.responses.UserData
import com.github.users.model.responses.UserRipoData
import com.github.users.utils.CheckInternetConnectionAvailable
import com.github.users.view.GithubUserListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Override ViewModel
class GithubUserViewModel : ViewModel() {

    // Over Activity Listener
    var githubUserListener: GithubUserListener? = null

    fun onUserInfo(context: Context, query: String) {
        // Check Internet connectivity
        if (context.CheckInternetConnectionAvailable()) {
            // API Calling Start

            // Ratrofit API Calling
            ApiClient().getUserInfo(query).enqueue(object : Callback<UserData> {

                // Success Response
                override fun onResponse(
                    call: Call<UserData>,
                    response: Response<UserData>
                ) {
                    if (response != null) {
                        if (response.body() != null) {
                            githubUserListener?.onSuccess(response.body()!! as UserData)
                        } else {
                            githubUserListener?.onSuccess(UserData())
                        }
                    } else {
                        githubUserListener?.onFailure(response.message())
                    }
                }

                // Failure Response
                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    githubUserListener?.onFailure("Fail ${t.message}")
                }

            })
        } else {
            // Internet is not connected
            githubUserListener?.onFailure("Please check your internet connection!")
        }
    }

    fun onUserRipo(context: Context, query: String) {
        // Check Internet connectivity
        if (context.CheckInternetConnectionAvailable()) {
            // API Calling Start
            githubUserListener?.showProgress()

            // Ratrofit API Calling
            ApiClient().getUserRipo(query).enqueue(object : Callback<List<UserRipoData>> {

                // Success Response
                override fun onResponse(
                    call: Call<List<UserRipoData>>,
                    response: Response<List<UserRipoData>>
                ) {
                    if (response != null) {
                        if (response.body() != null) {
                            githubUserListener?.onSuccess(response.body()!!)
                        } else {
                            githubUserListener?.onSuccess(UserData())
                        }
                    } else {
                        githubUserListener?.onFailure(response.message())
                    }

                    githubUserListener?.hideProgress()
                }

                // Failure Response
                override fun onFailure(call: Call<List<UserRipoData>>, t: Throwable) {
                    githubUserListener?.hideProgress()
                    githubUserListener?.onFailure("Fail ${t.message}")
                }

            })
        } else {
            // Internet is not connected
            githubUserListener?.hideProgress()
            githubUserListener?.onFailure("Please check your internet connection!")
        }
    }
}