package com.github.users.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.users.model.ApiClient
import com.github.users.model.responses.DataResponse
import com.github.users.model.responses.RowData
import com.github.users.utils.CheckInternetConnectionAvailable
import com.github.users.view.MainActivityListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Override ViewModel
class MainActivityViewModel : ViewModel() {

    // Over Activity Listener
    var mainActivityListener: MainActivityListener? = null

    fun onRefreshData(context: Context, query: String, page: Int) {
        // Check Internet connectivity
        if (context.CheckInternetConnectionAvailable()) {
            // API Calling Start
            mainActivityListener?.showProgress()

            // Ratrofit API Calling
            ApiClient().getUserSearch(query, page).enqueue(object : Callback<DataResponse> {

                // Success Response
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    if (response != null) {
                        if (response.body() != null) {
                            mainActivityListener?.onSuccess(response.body()!!.items!! as ArrayList<RowData>)
                        } else {
                            mainActivityListener?.onSuccess(ArrayList<RowData>())
                        }
                    } else {
                        mainActivityListener?.onFailure(response.message())
                    }

                    mainActivityListener?.hideProgress()
                }

                // Failure Response
                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    mainActivityListener?.hideProgress()
                    mainActivityListener?.onFailure("Fail ${t.message}")
                }

            })
        } else {
            // Internet is not connected
            mainActivityListener?.hideProgress()
            mainActivityListener?.onFailure("Please check your internet connection!")
        }
    }
}