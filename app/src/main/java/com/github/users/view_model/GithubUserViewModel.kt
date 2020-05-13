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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Override ViewModel
class GithubUserViewModel : ViewModel() {

    // Over Activity Listener
    var githubUserListener: GithubUserListener? = null
    private var myCompositeDisposable: CompositeDisposable? = null

    fun onUserInfo(context: Context, query: String) {
        // Check Internet connectivity
        if (context.CheckInternetConnectionAvailable()) {
            // Ratrofit API Calling
            myCompositeDisposable = CompositeDisposable()
            myCompositeDisposable?.add(
                ApiClient()
                    .getUserInfo(query)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response -> onResponse(response) }, { t -> onFailure(t) })
            )
        } else {
            // Internet is not connected
            githubUserListener?.onFailure("Please check your internet connection!")
        }
    }

    private fun onResponse(response: UserData) {
        githubUserListener?.hideProgress()
        githubUserListener?.onSuccess(response)
    }

    private fun onResponseList(response: List<UserRipoData>) {
        githubUserListener?.hideProgress()
        githubUserListener?.onSuccess(response)
    }

    private fun onFailure(error: Throwable) {
        githubUserListener?.hideProgress()
        githubUserListener?.onFailure("Fail ${error.message}")
    }

    fun onUserRipo(context: Context, query: String) {
        // Check Internet connectivity
        if (context.CheckInternetConnectionAvailable()) {
            // API Calling Start
            githubUserListener?.showProgress()

            // Ratrofit API Calling
            myCompositeDisposable = CompositeDisposable()
            myCompositeDisposable?.add(
                ApiClient()
                    .getUserRipo(query)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response -> onResponseList(response) }, { t -> onFailure(t) })
            )
        } else {
            // Internet is not connected
            githubUserListener?.hideProgress()
            githubUserListener?.onFailure("Please check your internet connection!")
        }
    }
}