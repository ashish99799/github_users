package com.github.users.view

import com.github.users.model.responses.RowData

interface MainActivityListener {
    fun showProgress()
    fun hideProgress()
    fun onSuccess(data: ArrayList<RowData>)
    fun onFailure(message: String)
}