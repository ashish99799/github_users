package com.github.users.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.users.R
import com.github.users.model.responses.RowData
import com.github.users.model.responses.UserData
import com.github.users.model.responses.UserRipoData
import com.github.users.utils.INTENT_DATA
import com.github.users.utils.LoadImage
import com.github.users.utils.ToastMessage
import com.github.users.view_model.GithubUserViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.github_user.*
import kotlinx.android.synthetic.main.github_user_cell.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GithubUser : AppCompatActivity(), GithubUserListener,
    SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    // Variable Declaration
    var context: Context? = null

    lateinit var hashMap: HashMap<String, Any>
    lateinit var rowData: RowData

    lateinit var originalList: ArrayList<UserRipoData>
    lateinit var ripoList: ArrayList<UserRipoData>

    var viewModel: GithubUserViewModel? = null
    var userRipoAdapter: UserRipoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.github_user)

        context = this

        hashMap = intent.getSerializableExtra(INTENT_DATA) as (java.util.HashMap<String, Any>)
        rowData = Gson().fromJson(hashMap.get("GithubUser").toString(), RowData::class.java)

        // Setup SupportActionBar to over Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = rowData.login

        initView()
    }

    fun initView() {
        LoadImage(rowData.avatar_url!!, imgAvatar)
        lblUserName.text = rowData.login

        // Attach ViewModel
        viewModel = ViewModelProvider(this).get(GithubUserViewModel::class.java)
        viewModel!!.githubUserListener = this

        rvUserRipo.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent)
        swipeRefreshLayout.isRefreshing = true

        ripoList = ArrayList<UserRipoData>()
        originalList = ArrayList<UserRipoData>()
        onRefresh()

        // Call Api User Info
        viewModel!!.onUserInfo(context!!, rowData.login!!)

        // Call Api User Ripo
        viewModel!!.onUserRipo(context!!, rowData.login!!)

        searchView.setOnQueryTextListener(this)
        val searchClose =
            searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as ImageView
        searchClose.setOnClickListener {
            searchView.clearFocus()
            searchView.setQuery("", false)
            onRefresh()
        }
    }

    override fun onRefresh() {
        ripoList = ArrayList<UserRipoData>()
        ripoList = originalList
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        userRipoAdapter!!.filter.filter(newText)
        return false
    }

    override fun showProgress() {
        // Api Calling started
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        // Api Calling ended
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onSuccess(data: UserData) {
        // Success API Response
        lblUserName.text = data.login ?: ""
        lblEmail.text = "Email : " + data.email ?: ""
        lblLocation.text = "Location : " + data.location ?: ""
        lblFollowers.text = "" + data.followers + " Followers"
        lblFollowing.text = "Following " + data.following

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val outputFormat = SimpleDateFormat("dd-MM-yyyy")

        val my_date = data.created_at
        val date: Date = inputFormat.parse(my_date)

        lblJoinDate.text = "Join Date : " + outputFormat.format(date)
    }

    override fun onSuccess(data: List<UserRipoData>) {
        // Success API Response
        if (ripoList.isEmpty()) {
            ripoList.addAll(data)
        }

        userRipoAdapter = UserRipoAdapter(context!!, ripoList)
        rvUserRipo.adapter = userRipoAdapter
    }

    override fun onFailure(message: String) {
        // Error & Failure
        swipeRefreshLayout.isRefreshing = false
        ToastMessage(message)
    }

    fun RipoFilter(query: String) {
        ripoList = ArrayList<UserRipoData>()
        ripoList = originalList
    }

    inner class UserRipoAdapter(
        private var context: Context,
        private val AdapterList: ArrayList<UserRipoData>
    ) : RecyclerView.Adapter<UserRipoAdapter.ViewHolder>(), Filterable {

        var FilterList = ArrayList<UserRipoData>()

        init {
            FilterList = AdapterList
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.github_user_cell, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return FilterList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.lblRipoName.text = FilterList[position].name
            holder.lblForks.text = "" + FilterList[position].forks + " Forks"
            holder.lblStars.text = "" + FilterList[position].stargazers_count + " Stars"
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var lblRipoName = view.lblRipoName
            var lblForks = view.lblForks
            var lblStars = view.lblStars
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val charSearch = constraint.toString()
                    if (charSearch.isEmpty()) {
                        FilterList = AdapterList
                    } else {
                        val resultList = ArrayList<UserRipoData>()
                        for (row in AdapterList) {
                            if (row.name!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                                resultList.add(row)
                            }
                        }
                        FilterList = resultList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = FilterList
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    FilterList = results?.values as ArrayList<UserRipoData>
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
