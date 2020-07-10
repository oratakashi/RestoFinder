package com.oratakashi.resto.ui.search

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.oratakashi.resto.BuildConfig
import com.oratakashi.resto.R
import com.oratakashi.resto.core.View
import com.oratakashi.resto.data.model.collection.DataCollections
import com.oratakashi.resto.data.model.main.DataMain
import com.oratakashi.resto.ui.search.SearchState.*
import com.oratakashi.resto.utils.LocationServices
import com.oratakashi.resto.utils.Utility
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.loading_collection.*
import kotlinx.android.synthetic.main.loading_search.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import javax.inject.Inject
import android.view.View as system
import com.oratakashi.resto.ui.search.CollectionState.Error as error
import com.oratakashi.resto.ui.search.CollectionState.Loading as loading
import com.oratakashi.resto.ui.search.CollectionState.Result as result

class SearchActivity : View() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val listCollection: MutableList<DataCollections> by lazy {
        ArrayList<DataCollections>()
    }
    private val listSearch: MutableList<DataMain> by lazy {
        ArrayList<DataMain>()
    }

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)
    }

    private val adapter: SearchAdapter by lazy {
        SearchAdapter(listSearch, this)
    }
    private val adapterCollection: CollectionAdapter by lazy {
        CollectionAdapter(listCollection, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        etSearch.requestFocus()
        etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (etSearch.text.toString().isNotEmpty()) {
                    getData(etSearch.text.toString())
                    Utility.dismisKeyboard(this)
                }
            }
            true
        }

        launch {

            /**
             * UI will show a Loading
             *
             * UI akan menampilkan Loading dan akan berhenti jika semua proses telah selesai
             */

            shCollection.startShimmerAnimation()

            getLocation()
        }

        viewModel.state.observe(this, Observer { state ->
            state?.let {
                when (it) {
                    is Loading -> {
                        llResult.visibility = system.GONE
                        shSearch.visibility = system.VISIBLE
                        shSearch.startShimmerAnimation()
                    }
                    is Result -> {
                        shSearch.stopShimmerAnimation()
                        shSearch.visibility = system.GONE
                        llResult.visibility = system.VISIBLE

                        listSearch.clear()
                        listSearch.addAll(it.data.data!!)
                        adapter.notifyDataSetChanged()

                        tvResult.text = String.format(
                            resources.getString(R.string.placeholder_search),
                            it.data.results_shown
                        )
                    }
                    is Error -> {
                        shSearch.stopShimmerAnimation()
                        shSearch.visibility = system.GONE
                        llResult.visibility = system.VISIBLE

                        val httpCode = it.error as HttpException

                        if (httpCode.code() != 400) {
                            if (BuildConfig.DEBUG) showMessage(it.error.message())

                            Snackbar.make(
                                clBase,
                                resources.getString(R.string.title_failed_load_data),
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(resources.getString(R.string.title_tryagain)) {
                                getData(etSearch.text.toString())
                            }
                        }
                    }
                }
            }
        })
        viewModel.collection.observe(this, Observer { state ->
            state?.let {
                when (it) {
                    is loading -> {
                        shCollection.visibility = system.VISIBLE
                        shCollection.startShimmerAnimation()
                        rvCollection.visibility = system.GONE
                        textView.visibility = system.VISIBLE
                        llLocation.visibility = system.VISIBLE
                    }
                    is result -> {
                        shCollection.stopShimmerAnimation()
                        shCollection.visibility = system.GONE
                        rvCollection.visibility = system.VISIBLE

                        listCollection.clear()
                        listCollection.addAll(it.data.data!!)
                        adapterCollection.notifyDataSetChanged()
                    }
                    is error -> {
                        shCollection.stopShimmerAnimation()
                        shCollection.visibility = system.GONE
                        rvCollection.visibility = system.VISIBLE
                        textView.visibility = system.GONE
                        llLocation.visibility = system.GONE

                        val httpCode = it.error as HttpException

                        if (httpCode.code() != 400) {
                            if (BuildConfig.DEBUG) showMessage(it.error.message())
                            Snackbar.make(
                                clBase,
                                resources.getString(R.string.title_failed_load_data),
                                Snackbar.LENGTH_INDEFINITE
                            ).setAction(resources.getString(R.string.title_tryagain)) {
                                getData()
                            }
                        }
                    }
                }
            }
        })

        rvResto.also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }
        rvCollection.also {
            it.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL, false
            )
            it.adapter = adapterCollection
        }

        getData()
        ivBack.setOnClickListener { onSupportNavigateUp() }
    }

    private fun getData() {
        GlobalScope.launch(Dispatchers.Default) {
            if (LocationServices.getLat(applicationContext).verify()) {
                viewModel.getCollection(
                    LocationServices.getLat(applicationContext),
                    LocationServices.getLang(applicationContext)
                )
            } else {
                getData()
            }
        }
    }

    private fun getData(keyword: String) {
        GlobalScope.launch(Dispatchers.Default) {
            if (LocationServices.getLat(applicationContext).verify()) {
                viewModel.getData(
                    keyword,
                    LocationServices.getLat(applicationContext),
                    LocationServices.getLang(applicationContext)
                )
            } else {
                getData(keyword)
            }
        }
    }

    private suspend fun getLocation() {

        /**
         * Give time apps to get Real Coordinate from GPS, if failed geting location,
         * apps will get last known lat and long
         *
         * Memberi jeda waktu untuk aplikasi mendapatkan titik koordinat
         * yang akurat dari GPS, dan jika gagal mendapatkan lokasi terbaru melalui GPS
         * Aplikasi akan mengambil titik gps terakhir di perangkat
         */

        if (!LocationServices.getLat(applicationContext).verify()) {
            if (LocationServices.retry > 3) {
                LocationServices.getLastLocation(applicationContext)
                delay(1000)
                Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.title_failed_get_location), Toast.LENGTH_SHORT
                ).show()
                getLocation()
            } else {
                delay(3000)
                getLocation()
            }
        } else {
            /**
             * Result Address store on Variables
             *
             * Hasil Alamat akan di simpan ke variabel dalam bentuk object
             */

            @Suppress("BlockingMethodInNonBlockingContext")
            val address = LocationServices.getLocationAddress(
                LocationServices.getLat(this).toDouble(),
                LocationServices.getLang(this).toDouble()
            )

            /**
             * Back to Main Thread, and Change UI
             *
             * Kembalikan ke Thread utama ketika proses sudah selesai
             * dan melakukan perubahan pada UI
             */

            withContext(Dispatchers.Main) {
                tvLocation2.text = address?.subAdminArea
                etSearch.hint = String.format(
                    resources.getString(R.string.placeholder_search_hint),
                    address?.subAdminArea
                )
            }
        }
    }

    override fun darkStyle(): Int {
        return R.style.AppThemeDark_NoActionBar
    }

    override fun lightStyle(): Int {
        return R.style.AppTheme_NoActionBar
    }
}