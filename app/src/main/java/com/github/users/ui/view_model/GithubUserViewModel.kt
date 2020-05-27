package com.github.users.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.users.model.api.ApiClient
import com.github.users.model.repositorys.GithubUserRepository
import com.github.users.model.responses.UserData
import com.github.users.model.responses.UserRipoData
import com.github.users.utils.CheckInternetConnectionAvailable
import com.github.users.ui.listeners.GithubUserListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// Override ViewModel
class GithubUserViewModel : ViewModel() {

    // Over Activity Listener
    var githubUserListener: GithubUserListener? = null
    private var myCompositeDisposable: CompositeDisposable? = null
    var githubUserRepository: GithubUserRepository

    init {
        githubUserRepository = GithubUserRepository(ApiClient())
    }

    fun onUserInfo(context: Context, query: String) {
        // Check Internet connectivity
        if (context.CheckInternetConnectionAvailable()) {
            // Ratrofit API Calling
            myCompositeDisposable = CompositeDisposable()
            myCompositeDisposable?.add(
                githubUserRepository
                    .onUserInfo(query)
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
                githubUserRepository
                    .onUserRipo(query)
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